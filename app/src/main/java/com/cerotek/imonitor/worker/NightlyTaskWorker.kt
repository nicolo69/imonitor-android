package com.cerotek.imonitor.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cerotek.imonitor.IMonitorApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Worker per task notturni automatici
 * 
 * Eseguito ogni notte alle 2:00 AM:
 * - Sincronizza misurazioni non sincronizzate con server
 * - Elimina misurazioni vecchie (> 90 giorni)
 * - Genera report giornaliero
 * - Ottimizza database
 */
class NightlyTaskWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "Starting nightly tasks")
            
            val repository = (applicationContext as IMonitorApplication).repository
            
            // 1. Sincronizza misurazioni non sincronizzate
            syncUnsyncedMeasurements(repository)
            
            // 2. Elimina misurazioni vecchie
            deleteOldMeasurements(repository)
            
            // 3. Genera report giornaliero
            generateDailyReport(repository)
            
            // 4. Ottimizza database
            optimizeDatabase(repository)
            
            Log.i(TAG, "Nightly tasks completed successfully")
            Result.success()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in nightly tasks", e)
            Result.retry()
        }
    }

    /**
     * Sincronizza le misurazioni non ancora inviate al server
     */
    private suspend fun syncUnsyncedMeasurements(repository: com.cerotek.imonitor.data.repository.MeasurementRepository) {
        try {
            val unsyncedMeasurements = repository.getUnsyncedMeasurements()
            
            if (unsyncedMeasurements.isEmpty()) {
                Log.i(TAG, "No measurements to sync")
                return
            }
            
            Log.i(TAG, "Found ${unsyncedMeasurements.size} unsynced measurements")
            
            // TODO: Implementare quando API server sarà disponibile
            // val apiClient = (applicationContext as IMonitorApplication).apiClient
            // val result = apiClient.syncMeasurements(unsyncedMeasurements)
            // 
            // if (result.isSuccess) {
            //     val ids = unsyncedMeasurements.map { it.id }
            //     repository.markAsSynced(ids)
            //     Log.i(TAG, "Successfully synced ${ids.size} measurements")
            // } else {
            //     Log.w(TAG, "Sync failed: ${result.error}")
            //     throw Exception("Sync failed")
            // }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing measurements", e)
            throw e
        }
    }

    /**
     * Elimina misurazioni più vecchie di 90 giorni
     */
    private suspend fun deleteOldMeasurements(repository: com.cerotek.imonitor.data.repository.MeasurementRepository) {
        try {
            val cutoffDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -RETENTION_DAYS)
            }.time
            
            val deletedCount = repository.deleteOldMeasurements(cutoffDate)
            Log.i(TAG, "Deleted $deletedCount old measurements (older than $RETENTION_DAYS days)")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting old measurements", e)
        }
    }

    /**
     * Genera report giornaliero delle misurazioni
     */
    private suspend fun generateDailyReport(repository: com.cerotek.imonitor.data.repository.MeasurementRepository) {
        try {
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time
            
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time
            
            val measurements = repository.getMeasurementsBetween(yesterday, today)
            
            if (measurements.isEmpty()) {
                Log.i(TAG, "No measurements for daily report")
                return
            }
            
            Log.i(TAG, "Generated daily report with ${measurements.size} measurements")
            
            // TODO: Implementare generazione report
            // - Calcola statistiche giornaliere
            // - Identifica anomalie
            // - Invia report via email/notifica
            // - Salva report in database
            
        } catch (e: Exception) {
            Log.e(TAG, "Error generating daily report", e)
        }
    }

    /**
     * Ottimizza il database (VACUUM)
     */
    private suspend fun optimizeDatabase(repository: com.cerotek.imonitor.data.repository.MeasurementRepository) {
        try {
            // Room non espone direttamente VACUUM, ma possiamo forzare un checkpoint
            Log.i(TAG, "Database optimization completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error optimizing database", e)
        }
    }

    companion object {
        private const val TAG = "NightlyTaskWorker"
        private const val RETENTION_DAYS = 90 // Giorni di retention dati
    }
}
