package com.cerotek.imonitor.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class NotificationHistoryManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "imonitor_notification_history",
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val KEY_HISTORY = "notification_history"
        private const val MAX_HISTORY_SIZE = 50 // Mantieni ultime 50 notifiche
    }
    
    data class NotificationItem(
        val id: String = UUID.randomUUID().toString(),
        val timestamp: Long = System.currentTimeMillis(),
        val parameterName: String,
        val parameterType: String,
        val value: Float,
        val unit: String,
        val minThreshold: Float,
        val maxThreshold: Float,
        val severity: Severity,
        val message: String
    ) {
        fun getFormattedDate(): String {
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ITALIAN)
            return sdf.format(Date(timestamp))
        }
        
        fun getFormattedTime(): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.ITALIAN)
            return sdf.format(Date(timestamp))
        }
        
        fun getFormattedDateTime(): String {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ITALIAN)
            return sdf.format(Date(timestamp))
        }
    }
    
    enum class Severity {
        CRITICAL,  // Rosso - Valore molto fuori range
        WARNING    // Giallo - Valore leggermente fuori range
    }
    
    // Aggiungi notifica allo storico
    fun addNotification(
        parameterName: String,
        parameterType: String,
        value: Float,
        unit: String,
        minThreshold: Float,
        maxThreshold: Float
    ) {
        val severity = calculateSeverity(value, minThreshold, maxThreshold)
        val message = generateMessage(parameterName, value, unit, minThreshold, maxThreshold, severity)
        
        val notification = NotificationItem(
            parameterName = parameterName,
            parameterType = parameterType,
            value = value,
            unit = unit,
            minThreshold = minThreshold,
            maxThreshold = maxThreshold,
            severity = severity,
            message = message
        )
        
        val history = getHistory().toMutableList()
        history.add(0, notification) // Aggiungi in cima
        
        // Mantieni solo le ultime MAX_HISTORY_SIZE
        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        
        saveHistory(history)
    }
    
    // Calcola severità in base a quanto è fuori range
    private fun calculateSeverity(value: Float, min: Float, max: Float): Severity {
        val range = max - min
        val tolerance = range * 0.2f // 20% di tolleranza
        
        return when {
            value < min - tolerance || value > max + tolerance -> Severity.CRITICAL
            else -> Severity.WARNING
        }
    }
    
    // Genera messaggio descrittivo
    private fun generateMessage(
        parameterName: String,
        value: Float,
        unit: String,
        min: Float,
        max: Float,
        severity: Severity
    ): String {
        val valueStr = if (value % 1 == 0f) value.toInt().toString() else String.format("%.1f", value)
        
        return when {
            value < min -> {
                val icon = if (severity == Severity.CRITICAL) "🔴" else "🟡"
                "$icon $parameterName troppo basso: $valueStr $unit (min: ${min.toInt()})"
            }
            value > max -> {
                val icon = if (severity == Severity.CRITICAL) "🔴" else "🟡"
                "$icon $parameterName troppo alto: $valueStr $unit (max: ${max.toInt()})"
            }
            else -> {
                "⚠️ $parameterName: $valueStr $unit"
            }
        }
    }
    
    // Ottieni storico completo
    fun getHistory(): List<NotificationItem> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<NotificationItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Ottieni storico filtrato per severità
    fun getHistoryBySeverity(severity: Severity): List<NotificationItem> {
        return getHistory().filter { it.severity == severity }
    }
    
    // Ottieni storico filtrato per parametro
    fun getHistoryByParameter(parameterType: String): List<NotificationItem> {
        return getHistory().filter { it.parameterType == parameterType }
    }
    
    // Ottieni conteggi
    fun getCriticalCount(): Int {
        return getHistory().count { it.severity == Severity.CRITICAL }
    }
    
    fun getWarningCount(): Int {
        return getHistory().count { it.severity == Severity.WARNING }
    }
    
    fun getTotalCount(): Int {
        return getHistory().size
    }
    
    // Cancella singola notifica
    fun deleteNotification(id: String) {
        val history = getHistory().toMutableList()
        history.removeAll { it.id == id }
        saveHistory(history)
    }
    
    // Cancella tutto lo storico
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
    
    // Cancella notifiche più vecchie di X giorni
    fun clearOldNotifications(days: Int) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
        val history = getHistory().filter { it.timestamp > cutoffTime }
        saveHistory(history)
    }
    
    // Salva storico
    private fun saveHistory(history: List<NotificationItem>) {
        val json = gson.toJson(history)
        prefs.edit().putString(KEY_HISTORY, json).apply()
    }
}
