package com.cerotek.imonitor.data.local.dao

import androidx.room.*
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MeasurementDao {
    
    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>
    
    @Query("SELECT * FROM measurements WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedMeasurements(): List<MeasurementEntity>
    
    @Query("SELECT * FROM measurements WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getMeasurementsByDateRange(startDate: Date, endDate: Date): Flow<List<MeasurementEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: MeasurementEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(measurements: List<MeasurementEntity>)
    
    @Update
    suspend fun update(measurement: MeasurementEntity)
    
    @Delete
    suspend fun delete(measurement: MeasurementEntity)
    
    @Query("DELETE FROM measurements")
    suspend fun deleteAll()
    
    @Query("UPDATE measurements SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)

    @Query("DELETE FROM measurements WHERE timestamp < :cutoffDate")
    suspend fun deleteOldMeasurements(cutoffDate: Date): Int

    @Query("SELECT * FROM measurements WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp ASC")
    suspend fun getMeasurementsBetweenDates(startDate: Date, endDate: Date): List<MeasurementEntity>
}
