package com.cerotek.imonitor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Date,
    val systolic: Int? = null,
    val diastolic: Int? = null,
    val heartRate: Int? = null,
    val oxygenSaturation: Int? = null,
    val temperature: Float? = null,
    val respiratoryRate: Int? = null,
    val bloodSugar: Float? = null,
    val hrv: Int? = null,
    val steps: Int? = null,
    val synced: Boolean = false
)
