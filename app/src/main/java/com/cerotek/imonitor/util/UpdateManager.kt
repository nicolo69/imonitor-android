package com.cerotek.imonitor.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cerotek.imonitor.BuildConfig
import com.cerotek.imonitor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class UpdateManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "imonitor_updates",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val TAG = "UpdateManager"
        private const val KEY_LAST_CHECK = "last_check_time"
        private const val KEY_AVAILABLE_VERSION = "available_version"
        private const val KEY_UPDATE_URL = "update_url"
        private const val KEY_AUTO_CHECK = "auto_check_enabled"
        private const val CHANNEL_ID = "updates_channel"
        private const val NOTIFICATION_ID = 100
        
        // URL per verificare aggiornamenti (da configurare)
        private const val UPDATE_CHECK_URL = "https://api.cerotek.com/imonitor/updates/check"
    }
    
    data class UpdateInfo(
        val available: Boolean,
        val currentVersion: String,
        val latestVersion: String,
        val downloadUrl: String?,
        val releaseNotes: String?,
        val mandatory: Boolean = false
    )
    
    // Abilita/disabilita controllo automatico
    fun setAutoCheckEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_CHECK, enabled).apply()
    }
    
    fun isAutoCheckEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_CHECK, true)
    }
    
    // Ottieni ultima versione disponibile salvata
    fun getAvailableVersion(): String? {
        return prefs.getString(KEY_AVAILABLE_VERSION, null)
    }
    
    // Ottieni URL download salvato
    fun getUpdateUrl(): String? {
        return prefs.getString(KEY_UPDATE_URL, null)
    }
    
    // Ottieni timestamp ultimo controllo
    fun getLastCheckTime(): Long {
        return prefs.getLong(KEY_LAST_CHECK, 0)
    }
    
    // Verifica se è disponibile un aggiornamento
    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Checking for updates...")
            
            // Aggiorna timestamp
            prefs.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply()
            
            // Versione corrente
            val currentVersion = BuildConfig.VERSION_NAME
            
            // Simula chiamata API (sostituire con chiamata reale)
            val updateInfo = checkUpdateFromServer(currentVersion)
            
            // Salva informazioni se disponibile aggiornamento
            if (updateInfo.available) {
                prefs.edit().apply {
                    putString(KEY_AVAILABLE_VERSION, updateInfo.latestVersion)
                    putString(KEY_UPDATE_URL, updateInfo.downloadUrl)
                    apply()
                }
                
                // Mostra notifica
                showUpdateNotification(updateInfo)
            }
            
            Log.d(TAG, "Update check completed: ${updateInfo.available}")
            updateInfo
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates", e)
            UpdateInfo(
                available = false,
                currentVersion = BuildConfig.VERSION_NAME,
                latestVersion = BuildConfig.VERSION_NAME,
                downloadUrl = null,
                releaseNotes = null
            )
        }
    }
    
    private fun checkUpdateFromServer(currentVersion: String): UpdateInfo {
        return try {
            // TODO: Implementare chiamata API reale
            // val connection = URL(UPDATE_CHECK_URL).openConnection()
            // val response = connection.getInputStream().bufferedReader().readText()
            // val json = JSONObject(response)
            
            // Per ora simula risposta
            val latestVersion = "1.3.0" // Versione più recente disponibile
            val available = isNewerVersion(currentVersion, latestVersion)
            
            UpdateInfo(
                available = available,
                currentVersion = currentVersion,
                latestVersion = latestVersion,
                downloadUrl = "https://download.cerotek.com/imonitor/v$latestVersion/imonitor.apk",
                releaseNotes = """
                    Novità versione $latestVersion:
                    • Miglioramenti prestazioni
                    • Correzione bug
                    • Nuove funzionalità dashboard
                    • Ottimizzazione batteria smartwatch
                """.trimIndent(),
                mandatory = false
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching update info", e)
            UpdateInfo(
                available = false,
                currentVersion = currentVersion,
                latestVersion = currentVersion,
                downloadUrl = null,
                releaseNotes = null
            )
        }
    }
    
    private fun isNewerVersion(current: String, latest: String): Boolean {
        try {
            val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }
            
            for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
                val currentPart = currentParts.getOrNull(i) ?: 0
                val latestPart = latestParts.getOrNull(i) ?: 0
                
                if (latestPart > currentPart) return true
                if (latestPart < currentPart) return false
            }
            
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error comparing versions", e)
            return false
        }
    }
    
    private fun showUpdateNotification(updateInfo: UpdateInfo) {
        createNotificationChannel()
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(updateInfo.downloadUrl)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎉 Aggiornamento Disponibile")
            .setContentText("Versione ${updateInfo.latestVersion} disponibile")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Nuova versione ${updateInfo.latestVersion} disponibile!\n\n${updateInfo.releaseNotes}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Scarica",
                pendingIntent
            )
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        Log.d(TAG, "Update notification shown")
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aggiornamenti App",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifiche per aggiornamenti disponibili"
                enableVibration(true)
            }
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    // Apri URL download
    fun openDownloadUrl() {
        val url = getUpdateUrl()
        if (url != null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
    
    // Formatta tempo ultimo controllo
    fun getLastCheckTimeFormatted(): String {
        val lastCheck = getLastCheckTime()
        if (lastCheck == 0L) return "Mai controllato"
        
        val now = System.currentTimeMillis()
        val diff = now - lastCheck
        
        return when {
            diff < 60_000 -> "Ora"
            diff < 3600_000 -> "${diff / 60_000} min fa"
            diff < 86400_000 -> "${diff / 3600_000} ore fa"
            else -> "${diff / 86400_000} giorni fa"
        }
    }
}
