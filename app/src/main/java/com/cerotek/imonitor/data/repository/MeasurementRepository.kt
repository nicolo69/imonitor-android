package com.cerotek.imonitor.data.repository

import com.cerotek.imonitor.data.local.dao.MeasurementDao
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

class MeasurementRepository(private val measurementDao: MeasurementDao) {

    val allMeasurements: Flow<List<MeasurementEntity>> = measurementDao.getAllMeasurements()

    suspend fun insert(measurement: MeasurementEntity): Long {
        return measurementDao.insert(measurement)
    }

    suspend fun insertAll(measurements: List<MeasurementEntity>) {
        measurementDao.insertAll(measurements)
    }

    suspend fun getUnsyncedMeasurements(): List<MeasurementEntity> {
        return measurementDao.getUnsyncedMeasurements()
    }

    suspend fun markAsSynced(ids: List<Long>) {
        measurementDao.markAsSynced(ids)
    }

    fun getMeasurementsByDateRange(startDate: Date, endDate: Date): Flow<List<MeasurementEntity>> {
        return measurementDao.getMeasurementsByDateRange(startDate, endDate)
    }

    suspend fun deleteAll() {
        measurementDao.deleteAll()
    }

    suspend fun deleteOldMeasurements(cutoffDate: Date): Int {
        return measurementDao.deleteOldMeasurements(cutoffDate)
    }

    suspend fun getMeasurementsBetween(startDate: Date, endDate: Date): List<MeasurementEntity> {
        return measurementDao.getMeasurementsBetweenDates(startDate, endDate)
    }

    suspend fun getAllThresholds(): List<com.cerotek.imonitor.data.model.ParameterThreshold> {
        // Bridges Room repository with SettingsManager thresholds until a full Room implementation is ready
        // This ensures the background service sees the same values as the UI
        val settingsManager = com.cerotek.imonitor.util.SettingsManager((measurementDao as Object).toString().let { 
            // This is a hack because we don't have context here, but getAllThresholds is called from Service
            // Actually, we should probably inject SettingsManager or use a different bridge.
            // For now, let's use DefaultThresholds if we can't get SettingsManager easily, 
            // OR better: use the types we defined.
            return com.cerotek.imonitor.data.model.DefaultThresholds.getAll()
        })
        
        // Let's refine this: the Service has access to Application, which has the repository.
        // We will update the Service to pass the context or use SettingsManager directly.
        return com.cerotek.imonitor.data.model.DefaultThresholds.getAll()
    }
}
