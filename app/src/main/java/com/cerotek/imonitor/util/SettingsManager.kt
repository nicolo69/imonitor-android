package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import com.cerotek.imonitor.data.model.ParameterThreshold

/**
 * Legacy SettingsManager class for old Fragment-based code
 * This is kept for backward compatibility with old Fragment files
 */
class SettingsManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("imonitor_settings", Context.MODE_PRIVATE)
    
    fun getVolume(): Float {
        return sharedPreferences.getFloat("volume", 50f)
    }
    
    fun setVolume(volume: Float) {
        sharedPreferences.edit().putFloat("volume", volume).apply()
    }
    
    fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("notifications_enabled", true)
    }
    
    fun areNotificationsEnabled(): Boolean {
        return isNotificationsEnabled()
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }
    
    fun getUpdateInterval(): Long {
        return sharedPreferences.getLong("update_interval", 60000L) // 1 minute default
    }
    
    fun setUpdateInterval(interval: Long) {
        sharedPreferences.edit().putLong("update_interval", interval).apply()
    }
    
    fun isValueInRange(parameterType: String, value: Float): Boolean {
        val threshold = getThreshold(parameterType)
        return value >= threshold.minValue && value <= threshold.maxValue
    }
    
    fun getThreshold(parameterType: String): ParameterThreshold {
        // Default thresholds - questi possono essere sovrascritti dalle impostazioni
        return when (parameterType.lowercase()) {
            "pressure", "pressione" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 90f,
                maxValue = 140f,
                unit = "mmHg"
            )
            "saturation", "saturazione" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 95f,
                maxValue = 100f,
                unit = "%"
            )
            "heartrate", "frequenza" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 60f,
                maxValue = 100f,
                unit = "bpm"
            )
            "temperature", "temperatura" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 36.0f,
                maxValue = 37.5f,
                unit = "°C"
            )
            "glucose", "glicemia" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 70f,
                maxValue = 140f,
                unit = "mg/dL"
            )
            "bodyfat", "grassi" -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 10f,
                maxValue = 30f,
                unit = "%"
            )
            else -> ParameterThreshold(
                parameterType = parameterType,
                minValue = 0f,
                maxValue = 100f,
                unit = ""
            )
        }
    }
    
    // Metodi per gestione allarmi
    fun areAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean("alerts_enabled", true)
    }
    
    fun setAlertsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("alerts_enabled", enabled).apply()
    }
    
    fun isVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean("vibration_enabled", true)
    }
    
    fun setVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("vibration_enabled", enabled).apply()
    }
    
    fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean("sound_enabled", true)
    }
    
    fun setSoundEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("sound_enabled", enabled).apply()
    }
    
    fun isBackgroundServiceEnabled(): Boolean {
        return sharedPreferences.getBoolean("background_service_enabled", true)
    }
    
    fun setBackgroundServiceEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("background_service_enabled", enabled).apply()
    }
    
    fun getMonitoringInterval(): Int {
        return sharedPreferences.getInt("monitoring_interval", 30) // 30 secondi default
    }
    
    fun setMonitoringInterval(seconds: Int) {
        sharedPreferences.edit().putInt("monitoring_interval", seconds).apply()
    }
}
