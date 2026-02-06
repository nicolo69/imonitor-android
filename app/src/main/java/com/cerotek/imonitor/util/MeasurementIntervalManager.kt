package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences

class MeasurementIntervalManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "imonitor_intervals",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_INTERVAL_PREFIX = "interval_"
        private const val DEFAULT_INTERVAL = 30 // minuti
        const val MIN_INTERVAL = 10 // minuti
        const val MAX_INTERVAL = 60 // minuti
    }
    
    // Salva intervallo per un parametro specifico
    fun setInterval(parameterType: String, minutes: Int) {
        val validMinutes = minutes.coerceIn(MIN_INTERVAL, MAX_INTERVAL)
        prefs.edit().putInt("${KEY_INTERVAL_PREFIX}$parameterType", validMinutes).apply()
    }
    
    // Ottieni intervallo per un parametro specifico
    fun getInterval(parameterType: String): Int {
        return prefs.getInt("${KEY_INTERVAL_PREFIX}$parameterType", DEFAULT_INTERVAL)
    }
    
    // Ottieni tutti gli intervalli
    fun getAllIntervals(): Map<String, Int> {
        return mapOf(
            "heart_rate" to getInterval("heart_rate"),
            "blood_pressure" to getInterval("blood_pressure"),
            "oxygen" to getInterval("oxygen"),
            "temperature" to getInterval("temperature"),
            "glucose" to getInterval("glucose"),
            "saturation" to getInterval("saturation")
        )
    }
    
    // Resetta tutti gli intervalli al default
    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
    
    // Formatta intervallo per display
    fun getIntervalFormatted(parameterType: String): String {
        val minutes = getInterval(parameterType)
        return when {
            minutes < 60 -> "$minutes min"
            minutes == 60 -> "1 ora"
            else -> "${minutes / 60} ore ${minutes % 60} min"
        }
    }
    
    // Ottieni descrizione intervallo
    fun getIntervalDescription(minutes: Int): String {
        return when {
            minutes <= 15 -> "Molto frequente"
            minutes <= 30 -> "Frequente"
            minutes <= 45 -> "Normale"
            else -> "Meno frequente"
        }
    }
}
