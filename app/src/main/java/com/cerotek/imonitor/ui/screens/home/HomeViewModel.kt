package com.cerotek.imonitor.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.ble.WatchMonitor
import com.cerotek.imonitor.ble.model.AllDataBean
import com.cerotek.imonitor.ble.model.WatchState
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import com.cerotek.imonitor.util.SettingsManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = (application as IMonitorApplication).repository
    private val watchMonitor = WatchMonitor(application)
    private val settingsManager = SettingsManager(application)
    
    // BLE Connection State
    private val _bleConnectionState = MutableStateFlow(BleConnectionState.DISCONNECTED)
    val bleConnectionState: StateFlow<BleConnectionState> = _bleConnectionState.asStateFlow()
    
    // Latest measurements
    private val _latestMeasurements = MutableStateFlow<AllDataBean?>(null)
    val latestMeasurements: StateFlow<AllDataBean?> = _latestMeasurements.asStateFlow()
    
    // Parameter statuses
    private val _parameterStatuses = MutableStateFlow<Map<String, ParameterStatus>>(emptyMap())
    val parameterStatuses: StateFlow<Map<String, ParameterStatus>> = _parameterStatuses.asStateFlow()
    
    init {
        watchMonitor.init()
        observeWatchState()
        observeMeasurementData()
    }
    
    private fun observeWatchState() {
        viewModelScope.launch {
            watchMonitor.watchState.collect { state ->
                _bleConnectionState.value = when {
                    WatchMonitor.isStateEnabled(state, WatchState.WATCH_CONNECTED) -> 
                        BleConnectionState.CONNECTED
                    WatchMonitor.isStateEnabled(state, WatchState.WATCH_FOUND) -> 
                        BleConnectionState.SEARCHING
                    else -> 
                        BleConnectionState.DISCONNECTED
                }
            }
        }
    }
    
    private fun observeMeasurementData() {
        viewModelScope.launch {
            watchMonitor.measurementData.collect { measurements ->
                if (measurements.isNotEmpty()) {
                    val latest = measurements.maxByOrNull { it.timestamp }
                    _latestMeasurements.value = latest
                    
                    // Save to database
                    saveMeasurements(measurements)
                    
                    // Update parameter statuses
                    latest?.let { updateParameterStatuses(it) }
                }
            }
        }
    }
    
    private suspend fun saveMeasurements(measurements: List<AllDataBean>) {
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
    }
    
    private fun updateParameterStatuses(data: AllDataBean) {
        val statuses = mutableMapOf<String, ParameterStatus>()
        
        // Heart Rate
        val heartRateThreshold = settingsManager.getThreshold("heart_rate")
        statuses["heart_rate"] = when {
            data.heartRate < heartRateThreshold.minValue -> ParameterStatus.WARNING
            data.heartRate > heartRateThreshold.maxValue -> ParameterStatus.ALERT
            else -> ParameterStatus.NORMAL
        }
        
        // Blood Pressure
        val pressureThreshold = settingsManager.getThreshold("blood_pressure")
        statuses["blood_pressure"] = when {
            data.systolic < pressureThreshold.minValue || data.diastolic < pressureThreshold.minValue -> 
                ParameterStatus.WARNING
            data.systolic > pressureThreshold.maxValue || data.diastolic > pressureThreshold.maxValue -> 
                ParameterStatus.ALERT
            else -> ParameterStatus.NORMAL
        }
        
        // Oxygen Saturation
        val saturationThreshold = settingsManager.getThreshold("oxygen_saturation")
        statuses["oxygen_saturation"] = when {
            data.oxygenSaturation < saturationThreshold.minValue -> ParameterStatus.ALERT
            data.oxygenSaturation < saturationThreshold.maxValue -> ParameterStatus.WARNING
            else -> ParameterStatus.NORMAL
        }
        
        // Temperature
        val temperatureThreshold = settingsManager.getThreshold("temperature")
        statuses["temperature"] = when {
            data.bodyTemperature < temperatureThreshold.minValue -> ParameterStatus.WARNING
            data.bodyTemperature > temperatureThreshold.maxValue -> ParameterStatus.ALERT
            else -> ParameterStatus.NORMAL
        }
        
        // Blood Sugar
        val bloodSugarThreshold = settingsManager.getThreshold("blood_sugar")
        statuses["blood_sugar"] = when {
            data.bloodSugarFloat < bloodSugarThreshold.minValue -> ParameterStatus.WARNING
            data.bloodSugarFloat > bloodSugarThreshold.maxValue -> ParameterStatus.ALERT
            else -> ParameterStatus.NORMAL
        }
        
        // Body Fat (using bodyFatInt + bodyFatFloat/10)
        val bodyFat = data.bodyFatInt + (data.bodyFatFloat / 10f)
        val bodyFatThreshold = settingsManager.getThreshold("body_fat")
        statuses["body_fat"] = when {
            bodyFat < bodyFatThreshold.minValue -> ParameterStatus.WARNING
            bodyFat > bodyFatThreshold.maxValue -> ParameterStatus.WARNING
            else -> ParameterStatus.NORMAL
        }
        
        _parameterStatuses.value = statuses
    }
    
    fun connectToWatch(macAddress: String) {
        watchMonitor.watchMacAddress = macAddress
        watchMonitor.scan()
    }
    
    fun retryConnection() {
        watchMonitor.scan()
    }
    
    override fun onCleared() {
        super.onCleared()
        watchMonitor.deInit()
    }
}

enum class BleConnectionState {
    CONNECTED,
    DISCONNECTED,
    NOT_FOUND,
    SEARCHING
}

enum class ParameterStatus {
    NORMAL,
    WARNING,
    ALERT
}
