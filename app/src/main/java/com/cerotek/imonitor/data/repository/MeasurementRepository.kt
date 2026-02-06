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
        // TODO: Implementare quando avremo la tabella thresholds
        // Per ora ritorna soglie di default
        return listOf(
            com.cerotek.imonitor.data.model.ParameterThreshold(
                parameterType = "heart_rate",
                minValue = 60f,
                maxValue = 100f,
                unit = "bpm"
            ),
            com.cerotek.imonitor.data.model.ParameterThreshold(
                parameterType = "blood_pressure",
                minValue = 90f,
                maxValue = 140f,
                unit = "mmHg"
            ),
            com.cerotek.imonitor.data.model.ParameterThreshold(
                parameterType = "oxygen_saturation",
                minValue = 95f,
                maxValue = 100f,
                unit = "%"
            ),
            com.cerotek.imonitor.data.model.ParameterThreshold(
                parameterType = "temperature",
                minValue = 36.0f,
                maxValue = 37.5f,
                unit = "°C"
            ),
            com.cerotek.imonitor.data.model.ParameterThreshold(
                parameterType = "blood_sugar",
                minValue = 70f,
                maxValue = 140f,
                unit = "mg/dL"
            )
        )
    }
}
