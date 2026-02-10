package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import com.cerotek.imonitor.data.model.ParameterThreshold
import com.cerotek.imonitor.data.model.ParameterTypes
import com.cerotek.imonitor.data.model.DefaultThresholds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Legacy SettingsManager class for old Fragment-based code
 * This is kept for backward compatibility with old Fragment files
 */
class SettingsManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    private val _isDarkThemeFlow = MutableStateFlow(sharedPreferences.getBoolean("dark_theme", false))
    val isDarkThemeFlow: StateFlow<Boolean> = _isDarkThemeFlow.asStateFlow()
    
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

    fun setThreshold(threshold: ParameterThreshold) {
        sharedPreferences.edit().apply {
            putFloat("${threshold.parameterType}_min", threshold.minValue)
            putFloat("${threshold.parameterType}_max", threshold.maxValue)
            putString("${threshold.parameterType}_unit", threshold.unit)
            apply()
        }
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
        val default = when (parameterType) {
            ParameterTypes.HEART_RATE -> DefaultThresholds.HEART_RATE
            ParameterTypes.BLOOD_PRESSURE_SYSTOLIC -> DefaultThresholds.BLOOD_PRESSURE_SYSTOLIC
            ParameterTypes.BLOOD_PRESSURE_DIASTOLIC -> DefaultThresholds.BLOOD_PRESSURE_DIASTOLIC
            ParameterTypes.OXYGEN_SATURATION -> DefaultThresholds.OXYGEN
            ParameterTypes.TEMPERATURE -> DefaultThresholds.TEMPERATURE
            ParameterTypes.BLOOD_SUGAR -> DefaultThresholds.GLUCOSE
            ParameterTypes.BODY_FAT -> DefaultThresholds.BODY_FAT
            else -> ParameterThreshold(parameterType, 0f, 100f, "")
        }

        return ParameterThreshold(
            parameterType = parameterType,
            minValue = sharedPreferences.getFloat("${parameterType}_min", default.minValue),
            maxValue = sharedPreferences.getFloat("${parameterType}_max", default.maxValue),
            unit = sharedPreferences.getString("${parameterType}_unit", default.unit) ?: default.unit
        )
    }
    
    // Metodi per gestione allarmi
    fun areAlertsEnabled(): Boolean {
        return sharedPreferences.getBoolean("alerts_enabled", true)
    }
    
    fun setAlertsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("alerts_enabled", enabled).apply()
    }

    fun isDarkTheme(): Boolean {
        return _isDarkThemeFlow.value
    }

    fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("dark_theme", enabled).apply()
        _isDarkThemeFlow.value = enabled
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
