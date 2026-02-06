package com.cerotek.imonitor.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.R
import com.cerotek.imonitor.ble.WatchMonitor
import com.cerotek.imonitor.ble.model.AllDataBean
import com.cerotek.imonitor.ble.model.WatchState
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import com.cerotek.imonitor.ui.main.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*

/**
 * Servizio in background per monitoraggio continuo dello smartwatch BLE
 * 
 * Funzionalità:
 * - Monitoraggio continuo anche con app chiusa
 * - Salvataggio automatico misurazioni
 * - Controllo soglie parametri
 * - Notifiche allarmi
 * - Sincronizzazione con server
 * - Ottimizzazione batteria
 */
class IMonitorService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    private lateinit var watchMonitor: WatchMonitor
    private var monitoringJob: Job? = null
    
    private val repository by lazy {
        (application as IMonitorApplication).repository
    }
    
    private val alertManager by lazy {
        com.cerotek.imonitor.util.AlertManager(this)
    }
    
    private var currentMonitoringInterval = MONITORING_INTERVAL_MS

    inner class LocalBinder : Binder() {
        fun getService(): IMonitorService = this@IMonitorService
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service created")
        
        createNotificationChannels()
        
        watchMonitor = WatchMonitor(this)
        watchMonitor.init()
        
        observeWatchState()
        observeMeasurementData()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service started")
        
        val notification = buildServiceNotification("Initializing...")
        startForeground(NOTIFICATION_ID, notification)
        
        val macAddress = intent?.getStringExtra(EXTRA_MAC_ADDRESS)
        startMonitoring(macAddress)
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Service destroyed")
        
        monitoringJob?.cancel()
        serviceScope.cancel()
        watchMonitor.deInit()
    }

    /**
     * Avvia il monitoraggio continuo dello smartwatch
     * @param macAddress Indirizzo MAC dello smartwatch (opzionale)
     */
    fun startMonitoring(macAddress: String? = null) {
        macAddress?.let {
            watchMonitor.watchMacAddress = it
            Log.i(TAG, "Monitoring smartwatch: $it")
        }
        
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    // Recupera dati storici dallo smartwatch
                    watchMonitor.retrieveHealthHistoryData()
                    
                    // Controlla batteria smartwatch
                    watchMonitor.checkBattery()
                    
                    // Attendi intervallo configurato
                    delay(currentMonitoringInterval)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in monitoring loop", e)
                    delay(ERROR_RETRY_INTERVAL_MS)
                }
            }
        }
    }

    /**
     * Ferma il monitoraggio
     */
    fun stopMonitoring() {
        monitoringJob?.cancel()
        Log.i(TAG, "Monitoring stopped")
    }

    /**
     * Aggiorna l'intervallo di monitoraggio
     * @param intervalSeconds Intervallo in secondi
     */
    fun updateMonitoringInterval(intervalSeconds: Int) {
        currentMonitoringInterval = intervalSeconds * 1000L
        Log.i(TAG, "Monitoring interval updated to $intervalSeconds seconds")
        
        // Riavvia monitoraggio con nuovo intervallo
        if (monitoringJob?.isActive == true) {
            val macAddress = watchMonitor.watchMacAddress
            stopMonitoring()
            startMonitoring(macAddress)
        }
    }

    /**
     * Osserva lo stato della connessione BLE
     */
    private fun observeWatchState() {
        serviceScope.launch {
            watchMonitor.watchState.collectLatest { state ->
                updateServiceNotification(state)
                handleStateChange(state)
                adjustMonitoringInterval(state)
            }
        }
    }

    /**
     * Osserva i dati delle misurazioni
     */
    private fun observeMeasurementData() {
        serviceScope.launch {
            watchMonitor.measurementData.collectLatest { measurements ->
                if (measurements.isNotEmpty()) {
                    Log.i(TAG, "Received ${measurements.size} measurements")
                    
                    // Controlla soglie per l'ultima misurazione
                    checkParameterThresholds(measurements.last())
                    
                    // Salva nel database locale
                    saveMeasurements(measurements)
                    
                    // Sincronizza con server (quando disponibile)
                    syncMeasurements()
                }
            }
        }
    }

    /**
     * Salva le misurazioni nel database locale
     */
    private suspend fun saveMeasurements(measurements: List<AllDataBean>) {
        try {
            val entities = measurements.map { bean ->
                MeasurementEntity(
                    timestamp = Date(bean.timestamp),
                    systolic = bean.systolic,
                    diastolic = bean.diastolic,
                    heartRate = bean.heartRate,
                    oxygenSaturation = bean.oxygenSaturation,
                    temperature = bean.bodyTemperature,
                    respiratoryRate = bean.respiratoryRate,
                    bloodSugar = bean.bloodSugarFloat,
                    hrv = bean.hrv,
                    steps = bean.steps,
                    synced = false
                )
            }
            
            repository.insertAll(entities)
            Log.i(TAG, "Saved ${entities.size} measurements to database")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error saving measurements", e)
        }
    }

    /**
     * Sincronizza le misurazioni con il server
     * Questa funzione sarà attiva quando l'API server sarà disponibile
     */
    private suspend fun syncMeasurements() {
        try {
            val unsyncedMeasurements = repository.getUnsyncedMeasurements()
            
            if (unsyncedMeasurements.isEmpty()) {
                Log.d(TAG, "No measurements to sync")
                return
            }
            
            Log.i(TAG, "Found ${unsyncedMeasurements.size} unsynced measurements")
            
            // TODO: Implementare quando API server sarà disponibile
            // val apiClient = (application as IMonitorApplication).apiClient
            // val result = apiClient.syncMeasurements(unsyncedMeasurements)
            // 
            // if (result.isSuccess) {
            //     val ids = unsyncedMeasurements.map { it.id }
            //     repository.markAsSynced(ids)
            //     watchMonitor.deleteHistoricalMeasures()
            //     Log.i(TAG, "Successfully synced ${ids.size} measurements")
            // } else {
            //     Log.w(TAG, "Sync failed: ${result.error}")
            // }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing measurements", e)
        }
    }

    /**
     * Controlla se i parametri superano le soglie configurate
     * Invia notifiche in caso di valori critici
     */
    private suspend fun checkParameterThresholds(measurement: AllDataBean) {
        try {
            val thresholds = repository.getAllThresholds()
            
            // Frequenza cardiaca
            thresholds.find { it.parameterType == "heart_rate" }?.let { threshold ->
                measurement.heartRate?.let { hr ->
                    when {
                        hr < threshold.minValue -> {
                            alertManager.sendWarningAlert(
                                parameterName = "Frequenza Cardiaca",
                                currentValue = "$hr bpm",
                                thresholdValue = "minimo ${threshold.minValue.toInt()} bpm"
                            )
                        }
                        hr > threshold.maxValue -> {
                            alertManager.sendCriticalAlert(
                                parameterName = "Frequenza Cardiaca",
                                currentValue = "$hr bpm",
                                thresholdValue = "massimo ${threshold.maxValue.toInt()} bpm"
                            )
                        }
                    }
                }
            }
            
            // Pressione sanguigna
            thresholds.find { it.parameterType == "blood_pressure" }?.let { threshold ->
                measurement.systolic?.let { sys ->
                    when {
                        sys < threshold.minValue -> {
                            alertManager.sendWarningAlert(
                                parameterName = "Pressione Sanguigna",
                                currentValue = "$sys mmHg",
                                thresholdValue = "minimo ${threshold.minValue.toInt()} mmHg"
                            )
                        }
                        sys > threshold.maxValue -> {
                            alertManager.sendCriticalAlert(
                                parameterName = "Pressione Sanguigna",
                                currentValue = "$sys mmHg",
                                thresholdValue = "massimo ${threshold.maxValue.toInt()} mmHg"
                            )
                        }
                    }
                }
            }
            
            // Saturazione ossigeno
            thresholds.find { it.parameterType == "oxygen_saturation" }?.let { threshold ->
                measurement.oxygenSaturation?.let { spo2 ->
                    if (spo2 < threshold.minValue) {
                        alertManager.sendCriticalAlert(
                            parameterName = "Saturazione Ossigeno",
                            currentValue = "$spo2%",
                            thresholdValue = "minimo ${threshold.minValue.toInt()}%"
                        )
                    }
                }
            }
            
            // Temperatura corporea
            thresholds.find { it.parameterType == "temperature" }?.let { threshold ->
                measurement.bodyTemperature?.let { temp ->
                    when {
                        temp < threshold.minValue -> {
                            alertManager.sendWarningAlert(
                                parameterName = "Temperatura",
                                currentValue = "%.1f°C".format(temp),
                                thresholdValue = "minimo %.1f°C".format(threshold.minValue)
                            )
                        }
                        temp > threshold.maxValue -> {
                            alertManager.sendCriticalAlert(
                                parameterName = "Temperatura",
                                currentValue = "%.1f°C".format(temp),
                                thresholdValue = "massimo %.1f°C".format(threshold.maxValue)
                            )
                        }
                    }
                }
            }
            
            // Glicemia
            thresholds.find { it.parameterType == "blood_sugar" }?.let { threshold ->
                measurement.bloodSugarFloat?.let { sugar ->
                    when {
                        sugar < threshold.minValue -> {
                            alertManager.sendWarningAlert(
                                parameterName = "Glicemia",
                                currentValue = "%.1f mg/dL".format(sugar),
                                thresholdValue = "minimo %.1f mg/dL".format(threshold.minValue)
                            )
                        }
                        sugar > threshold.maxValue -> {
                            alertManager.sendCriticalAlert(
                                parameterName = "Glicemia",
                                currentValue = "%.1f mg/dL".format(sugar),
                                thresholdValue = "massimo %.1f mg/dL".format(threshold.maxValue)
                            )
                        }
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking thresholds", e)
        }
    }

    /**
     * Gestisce i cambiamenti di stato della connessione BLE
     */
    private fun handleStateChange(state: Int) {
        val isConnected = WatchMonitor.isStateEnabled(state, WatchState.WATCH_CONNECTED)
        val isFound = WatchMonitor.isStateEnabled(state, WatchState.WATCH_FOUND)
        val isBatteryLow = WatchMonitor.isStateEnabled(state, WatchState.WATCH_BATTERY_LOW)
        val isNotWear = WatchMonitor.isStateEnabled(state, WatchState.WATCH_NOT_WEAR)
        
        Log.i(TAG, "State changed - Connected: $isConnected, Found: $isFound, " +
                "BatteryLow: $isBatteryLow, NotWear: $isNotWear")
        
        // Notifica batteria bassa smartwatch
        if (isBatteryLow) {
            alertManager.sendDeviceAlert(
                type = com.cerotek.imonitor.util.AlertType.DEVICE_BATTERY_LOW,
                message = "Ricaricare lo smartwatch per continuare il monitoraggio"
            )
        }
        
        // Notifica smartwatch non indossato
        if (isNotWear) {
            alertManager.sendDeviceAlert(
                type = com.cerotek.imonitor.util.AlertType.DEVICE_NOT_WORN,
                message = "Indossare lo smartwatch per misurazioni accurate"
            )
        }
        
        // Notifica disconnessione prolungata
        if (!isConnected && !isFound) {
            serviceScope.launch {
                delay(DISCONNECTION_ALERT_DELAY_MS)
                if (!WatchMonitor.isStateEnabled(watchMonitor.watchState.value, WatchState.WATCH_CONNECTED)) {
                    alertManager.sendDeviceAlert(
                        type = com.cerotek.imonitor.util.AlertType.DEVICE_DISCONNECTED,
                        message = "Verificare la connessione Bluetooth"
                    )
                }
            }
        }
    }

    /**
     * Adatta l'intervallo di monitoraggio in base allo stato
     * Ottimizza il consumo batteria
     */
    private fun adjustMonitoringInterval(state: Int) {
        val newInterval = when {
            // Connesso e tutto ok: intervallo normale
            WatchMonitor.isStateEnabled(state, WatchState.WATCH_CONNECTED) -> 
                MONITORING_INTERVAL_MS
            
            // Disconnesso: intervallo più lungo per risparmiare batteria
            !WatchMonitor.isStateEnabled(state, WatchState.WATCH_FOUND) -> 
                MONITORING_INTERVAL_MS * 4
            
            // Batteria bassa: intervallo molto lungo
            WatchMonitor.isStateEnabled(state, WatchState.WATCH_BATTERY_LOW) -> 
                MONITORING_INTERVAL_MS * 10
            
            else -> MONITORING_INTERVAL_MS
        }
        
        if (newInterval != currentMonitoringInterval) {
            currentMonitoringInterval = newInterval
            Log.i(TAG, "Adjusted monitoring interval to ${newInterval / 1000}s")
        }
    }

    /**
     * Aggiorna la notifica del servizio in base allo stato
     */
    private fun updateServiceNotification(state: Int) {
        val statusText = when {
            WatchMonitor.isStateEnabled(state, WatchState.WATCH_CONNECTED) -> "Connesso - Monitoraggio attivo"
            WatchMonitor.isStateEnabled(state, WatchState.WATCH_FOUND) -> "Dispositivo trovato"
            else -> "Ricerca dispositivo..."
        }
        
        val notification = buildServiceNotification(statusText)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Costruisce la notifica persistente del servizio
     */
    private fun buildServiceNotification(statusText: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("iMonitor")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    /**
     * Invia una notifica di allarme
     */
    private fun sendAlertNotification(
        title: String,
        message: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()
        
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
        
        Log.i(TAG, "Alert notification sent: $title")
    }

    /**
     * Crea i canali di notifica
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // Canale per servizio background
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Servizio Monitoraggio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifica persistente del servizio di monitoraggio"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(serviceChannel)
            
            // Canale per allarmi critici
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Allarmi Salute",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifiche per parametri critici"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        private const val TAG = "IMonitorService"
        
        // Notification channels
        private const val CHANNEL_ID = "imonitor_service_channel"
        private const val ALERT_CHANNEL_ID = "imonitor_alert_channel"
        
        // Notification IDs
        private const val NOTIFICATION_ID = 1
        
        // Intervalli (millisecondi)
        private const val MONITORING_INTERVAL_MS = 30_000L // 30 secondi
        private const val ERROR_RETRY_INTERVAL_MS = 60_000L // 1 minuto
        private const val DISCONNECTION_ALERT_DELAY_MS = 300_000L // 5 minuti
        
        // Intent extras
        const val EXTRA_MAC_ADDRESS = "mac_address"
    }
}
