package com.cerotek.imonitor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cerotek.imonitor.R
import com.cerotek.imonitor.ui.main.MainActivity
import java.util.*

/**
 * Gestione completa del sistema di allarmi
 * 
 * Funzionalità:
 * - Notifiche con priorità dinamica
 * - Suoni e vibrazioni personalizzate
 * - Storico allarmi
 * - Configurazione utente
 * - Rate limiting (evita spam)
 */
class AlertManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val settingsManager = SettingsManager(context)
    private val notificationHistory = NotificationHistoryManager(context)
    
    // Rate limiting: evita troppe notifiche dello stesso tipo
    private val lastNotificationTime = mutableMapOf<AlertType, Long>()

    init {
        createNotificationChannels()
    }

    /**
     * Invia un allarme con configurazione completa
     */
    fun sendAlert(
        type: AlertType,
        parameterName: String,
        currentValue: String,
        thresholdValue: String,
        severity: AlertSeverity = AlertSeverity.HIGH
    ) {
        // Verifica se gli allarmi sono abilitati
        if (!settingsManager.areAlertsEnabled()) {
            return
        }

        // Rate limiting: evita spam
        if (!shouldSendNotification(type)) {
            return
        }

        // Costruisci messaggio
        val (title, message) = buildAlertMessage(type, parameterName, currentValue, thresholdValue)

        // Invia notifica
        sendNotification(type, title, message, severity)

        // Vibrazione
        if (settingsManager.isVibrationEnabled()) {
            vibrateForAlert(severity)
        }

        // Suono
        if (settingsManager.isSoundEnabled()) {
            // Il suono è gestito automaticamente dalla notifica
        }

        // Salva nello storico
        saveToHistory(type, title, message, severity, parameterName, currentValue, thresholdValue)

        // Aggiorna timestamp ultima notifica
        lastNotificationTime[type] = System.currentTimeMillis()
    }
    
    /**
     * Salva l'allarme nello storico
     */
    private fun saveToHistory(
        type: AlertType,
        title: String,
        message: String,
        severity: AlertSeverity,
        parameterName: String,
        currentValue: String,
        thresholdValue: String
    ) {
        try {
            // Estrai valore numerico e unità da currentValue (es. "120 bpm")
            val valueParts = currentValue.split(" ")
            val value = valueParts.getOrNull(0)?.toFloatOrNull() ?: 0f
            val unit = valueParts.getOrNull(1) ?: ""
            
            // Estrai soglia da thresholdValue (es. "massimo 100 bpm")
            val thresholdParts = thresholdValue.split(" ")
            val thresholdNum = thresholdParts.lastOrNull { it.toFloatOrNull() != null }?.toFloatOrNull() ?: 0f
            
            // Determina min/max in base al tipo di allarme
            val (minThreshold, maxThreshold) = when {
                thresholdValue.contains("massimo") -> Pair(0f, thresholdNum)
                thresholdValue.contains("minimo") -> Pair(thresholdNum, 999f)
                else -> Pair(0f, thresholdNum)
            }
            
            // Salva usando il metodo del NotificationHistoryManager
            notificationHistory.addNotification(
                parameterName = parameterName,
                parameterType = type.name,
                value = value,
                unit = unit,
                minThreshold = minThreshold,
                maxThreshold = maxThreshold
            )
        } catch (e: Exception) {
            Log.e("AlertManager", "Error saving to history", e)
        }
    }

    /**
     * Invia allarme critico (massima priorità)
     */
    fun sendCriticalAlert(
        parameterName: String,
        currentValue: String,
        thresholdValue: String
    ) {
        sendAlert(
            type = AlertType.CRITICAL_VALUE,
            parameterName = parameterName,
            currentValue = currentValue,
            thresholdValue = thresholdValue,
            severity = AlertSeverity.CRITICAL
        )
    }

    /**
     * Invia allarme warning (priorità media)
     */
    fun sendWarningAlert(
        parameterName: String,
        currentValue: String,
        thresholdValue: String
    ) {
        sendAlert(
            type = AlertType.WARNING_VALUE,
            parameterName = parameterName,
            currentValue = currentValue,
            thresholdValue = thresholdValue,
            severity = AlertSeverity.MEDIUM
        )
    }

    /**
     * Invia allarme dispositivo
     */
    fun sendDeviceAlert(type: AlertType, message: String) {
        if (!settingsManager.areAlertsEnabled()) return

        val title = when (type) {
            AlertType.DEVICE_DISCONNECTED -> "📡 Dispositivo Disconnesso"
            AlertType.DEVICE_BATTERY_LOW -> "🔋 Batteria Bassa"
            AlertType.DEVICE_NOT_WORN -> "⌚ Dispositivo Non Indossato"
            else -> "⚠️ Allarme Dispositivo"
        }

        sendNotification(type, title, message, AlertSeverity.LOW)
        
        // Salva nello storico con valori di default per dispositivo
        try {
            notificationHistory.addNotification(
                parameterName = title,
                parameterType = type.name,
                value = 0f,
                unit = "",
                minThreshold = 0f,
                maxThreshold = 0f
            )
        } catch (e: Exception) {
            Log.e("AlertManager", "Error saving device alert to history", e)
        }
    }

    /**
     * Verifica se inviare la notifica (rate limiting)
     */
    private fun shouldSendNotification(type: AlertType): Boolean {
        val lastTime = lastNotificationTime[type] ?: 0
        val currentTime = System.currentTimeMillis()
        val minInterval = getMinNotificationInterval(type)
        
        return (currentTime - lastTime) >= minInterval
    }

    /**
     * Ottiene l'intervallo minimo tra notifiche dello stesso tipo
     */
    private fun getMinNotificationInterval(type: AlertType): Long {
        return when (type) {
            AlertType.CRITICAL_VALUE -> 2 * 60 * 1000L // 2 minuti
            AlertType.WARNING_VALUE -> 5 * 60 * 1000L // 5 minuti
            AlertType.DEVICE_DISCONNECTED -> 10 * 60 * 1000L // 10 minuti
            AlertType.DEVICE_BATTERY_LOW -> 30 * 60 * 1000L // 30 minuti
            AlertType.DEVICE_NOT_WORN -> 15 * 60 * 1000L // 15 minuti
        }
    }

    /**
     * Costruisce il messaggio dell'allarme
     */
    private fun buildAlertMessage(
        type: AlertType,
        parameterName: String,
        currentValue: String,
        thresholdValue: String
    ): Pair<String, String> {
        return when (type) {
            AlertType.CRITICAL_VALUE -> {
                val title = "🚨 $parameterName Critico"
                val message = "Valore attuale: $currentValue\nSoglia: $thresholdValue\n\nContattare immediatamente il medico!"
                Pair(title, message)
            }
            AlertType.WARNING_VALUE -> {
                val title = "⚠️ $parameterName Anomalo"
                val message = "Valore attuale: $currentValue\nSoglia: $thresholdValue\n\nMonitorare attentamente."
                Pair(title, message)
            }
            else -> Pair("Allarme", "Valore: $currentValue")
        }
    }

    /**
     * Invia la notifica
     */
    private fun sendNotification(
        type: AlertType,
        title: String,
        message: String,
        severity: AlertSeverity
    ) {
        val channelId = getChannelIdForSeverity(severity)
        val priority = getPriorityForSeverity(severity)

        // Intent per aprire l'app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("alert_type", type.name)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            type.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Costruisci notifica
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(getIconForType(type))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        // Aggiungi suono se abilitato
        if (settingsManager.isSoundEnabled()) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder.setSound(soundUri)
        }

        // Aggiungi vibrazione se abilitata
        if (settingsManager.isVibrationEnabled()) {
            builder.setVibrate(getVibrationPattern(severity))
        }

        // Colore LED per allarmi critici
        if (severity == AlertSeverity.CRITICAL) {
            builder.setLights(0xFFFF0000.toInt(), 1000, 500)
        }

        // Invia notifica con ID univoco
        val notificationId = generateNotificationId(type)
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Vibrazione in base alla severità
     */
    private fun vibrateForAlert(severity: AlertSeverity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (severity) {
                AlertSeverity.CRITICAL -> VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 200, 500, 200, 500),
                    -1
                )
                AlertSeverity.HIGH -> VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 200, 500),
                    -1
                )
                AlertSeverity.MEDIUM -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                AlertSeverity.LOW -> VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(getVibrationPattern(severity), -1)
        }
    }

    /**
     * Pattern di vibrazione
     */
    private fun getVibrationPattern(severity: AlertSeverity): LongArray {
        return when (severity) {
            AlertSeverity.CRITICAL -> longArrayOf(0, 500, 200, 500, 200, 500)
            AlertSeverity.HIGH -> longArrayOf(0, 500, 200, 500)
            AlertSeverity.MEDIUM -> longArrayOf(0, 500)
            AlertSeverity.LOW -> longArrayOf(0, 200)
        }
    }

    /**
     * Crea i canali di notifica
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canale allarmi critici
            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL,
                "Allarmi Critici",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Allarmi per valori critici che richiedono attenzione immediata"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500, 200, 500)
                enableLights(true)
                lightColor = 0xFFFF0000.toInt()
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            // Canale allarmi alta priorità
            val highChannel = NotificationChannel(
                CHANNEL_HIGH,
                "Allarmi Importanti",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Allarmi per valori anomali che richiedono attenzione"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                enableLights(true)
            }

            // Canale allarmi media priorità
            val mediumChannel = NotificationChannel(
                CHANNEL_MEDIUM,
                "Avvisi",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avvisi per valori da monitorare"
                enableVibration(true)
            }

            // Canale allarmi bassa priorità
            val lowChannel = NotificationChannel(
                CHANNEL_LOW,
                "Notifiche Dispositivo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifiche relative allo stato del dispositivo"
            }

            notificationManager.createNotificationChannels(
                listOf(criticalChannel, highChannel, mediumChannel, lowChannel)
            )
        }
    }

    /**
     * Ottiene il channel ID in base alla severità
     */
    private fun getChannelIdForSeverity(severity: AlertSeverity): String {
        return when (severity) {
            AlertSeverity.CRITICAL -> CHANNEL_CRITICAL
            AlertSeverity.HIGH -> CHANNEL_HIGH
            AlertSeverity.MEDIUM -> CHANNEL_MEDIUM
            AlertSeverity.LOW -> CHANNEL_LOW
        }
    }

    /**
     * Ottiene la priorità in base alla severità
     */
    private fun getPriorityForSeverity(severity: AlertSeverity): Int {
        return when (severity) {
            AlertSeverity.CRITICAL -> NotificationCompat.PRIORITY_MAX
            AlertSeverity.HIGH -> NotificationCompat.PRIORITY_HIGH
            AlertSeverity.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            AlertSeverity.LOW -> NotificationCompat.PRIORITY_LOW
        }
    }

    /**
     * Ottiene l'icona in base al tipo
     */
    private fun getIconForType(type: AlertType): Int {
        return when (type) {
            AlertType.CRITICAL_VALUE -> R.drawable.ic_launcher_foreground
            AlertType.WARNING_VALUE -> R.drawable.ic_launcher_foreground
            AlertType.DEVICE_DISCONNECTED -> R.drawable.ic_device_notfound
            AlertType.DEVICE_BATTERY_LOW -> R.drawable.ic_device_battery
            AlertType.DEVICE_NOT_WORN -> R.drawable.ic_device_notfound
        }
    }

    /**
     * Genera ID univoco per la notifica
     */
    private fun generateNotificationId(type: AlertType): Int {
        return (NOTIFICATION_ID_BASE + type.ordinal + (System.currentTimeMillis() % 1000).toInt())
    }

    /**
     * Cancella tutte le notifiche
     */
    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }

    /**
     * Cancella notifiche di un tipo specifico
     */
    fun clearNotificationsOfType(type: AlertType) {
        // Android non permette di cancellare per tipo, ma possiamo tracciare gli ID
        // Per ora cancelliamo tutte
        notificationManager.cancelAll()
    }

    companion object {
        private const val CHANNEL_CRITICAL = "alerts_critical"
        private const val CHANNEL_HIGH = "alerts_high"
        private const val CHANNEL_MEDIUM = "alerts_medium"
        private const val CHANNEL_LOW = "alerts_low"
        
        private const val NOTIFICATION_ID_BASE = 1000
    }
}

/**
 * Tipi di allarme
 */
enum class AlertType {
    CRITICAL_VALUE,      // Valore critico parametro
    WARNING_VALUE,       // Valore anomalo parametro
    DEVICE_DISCONNECTED, // Dispositivo disconnesso
    DEVICE_BATTERY_LOW,  // Batteria dispositivo bassa
    DEVICE_NOT_WORN      // Dispositivo non indossato
}

/**
 * Severità allarme
 */
enum class AlertSeverity {
    CRITICAL,  // Richiede azione immediata
    HIGH,      // Richiede attenzione
    MEDIUM,    // Da monitorare
    LOW        // Informativo
}
