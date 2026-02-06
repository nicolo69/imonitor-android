package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WatchBatteryManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "imonitor_watch_battery",
        Context.MODE_PRIVATE
    )
    
    private val _batteryLevel = MutableStateFlow(getBatteryLevel())
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()
    
    private val _isCharging = MutableStateFlow(isWatchCharging())
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()
    
    private val _lastUpdate = MutableStateFlow(getLastUpdateTime())
    val lastUpdate: StateFlow<Long> = _lastUpdate.asStateFlow()
    
    companion object {
        private const val KEY_BATTERY_LEVEL = "battery_level"
        private const val KEY_IS_CHARGING = "is_charging"
        private const val KEY_LAST_UPDATE = "last_update"
        private const val KEY_WATCH_CONNECTED = "watch_connected"
    }
    
    fun updateBatteryLevel(level: Int, isCharging: Boolean = false) {
        prefs.edit().apply {
            putInt(KEY_BATTERY_LEVEL, level)
            putBoolean(KEY_IS_CHARGING, isCharging)
            putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
            apply()
        }
        
        _batteryLevel.value = level
        _isCharging.value = isCharging
        _lastUpdate.value = System.currentTimeMillis()
    }
    
    fun getBatteryLevel(): Int {
        return prefs.getInt(KEY_BATTERY_LEVEL, -1)
    }
    
    fun isWatchCharging(): Boolean {
        return prefs.getBoolean(KEY_IS_CHARGING, false)
    }
    
    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE, 0)
    }
    
    fun setWatchConnected(connected: Boolean) {
        prefs.edit().putBoolean(KEY_WATCH_CONNECTED, connected).apply()
    }
    
    fun isWatchConnected(): Boolean {
        return prefs.getBoolean(KEY_WATCH_CONNECTED, false)
    }
    
    fun getBatteryStatus(): BatteryStatus {
        val level = getBatteryLevel()
        val charging = isWatchCharging()
        val connected = isWatchConnected()
        
        return when {
            !connected -> BatteryStatus.DISCONNECTED
            level < 0 -> BatteryStatus.UNKNOWN
            charging -> BatteryStatus.CHARGING
            level <= 10 -> BatteryStatus.CRITICAL
            level <= 20 -> BatteryStatus.LOW
            level <= 50 -> BatteryStatus.MEDIUM
            else -> BatteryStatus.GOOD
        }
    }
    
    fun getBatteryStatusText(): String {
        val level = getBatteryLevel()
        val status = getBatteryStatus()
        
        return when (status) {
            BatteryStatus.DISCONNECTED -> "Smartwatch non connesso"
            BatteryStatus.UNKNOWN -> "Livello batteria sconosciuto"
            BatteryStatus.CHARGING -> "$level% (In carica)"
            BatteryStatus.CRITICAL -> "$level% (Critico)"
            BatteryStatus.LOW -> "$level% (Basso)"
            BatteryStatus.MEDIUM -> "$level%"
            BatteryStatus.GOOD -> "$level%"
        }
    }
    
    fun getBatteryIcon(): String {
        return when (getBatteryStatus()) {
            BatteryStatus.DISCONNECTED -> "🔌"
            BatteryStatus.UNKNOWN -> "❓"
            BatteryStatus.CHARGING -> "⚡"
            BatteryStatus.CRITICAL -> "🪫"
            BatteryStatus.LOW -> "🔋"
            BatteryStatus.MEDIUM -> "🔋"
            BatteryStatus.GOOD -> "🔋"
        }
    }
    
    enum class BatteryStatus {
        DISCONNECTED,
        UNKNOWN,
        CHARGING,
        CRITICAL,
        LOW,
        MEDIUM,
        GOOD
    }
}
