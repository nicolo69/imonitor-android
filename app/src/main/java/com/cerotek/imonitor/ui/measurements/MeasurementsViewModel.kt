package com.cerotek.imonitor.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.data.local.entity.MeasurementEntity
import com.cerotek.imonitor.network.MockInterceptor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Date

class MeasurementsViewModel : ViewModel() {

    private val repository = IMonitorApplication.instance.repository

    val measurements: StateFlow<List<MeasurementEntity>> = if (MockInterceptor.ENABLED) {
        // Mock data
        MutableStateFlow(generateMockMeasurements())
    } else {
        // Real data from database
        repository.allMeasurements
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
    
    private fun generateMockMeasurements(): List<MeasurementEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MeasurementEntity(
                id = 1,
                timestamp = Date(now - 3600000), // 1 ora fa
                heartRate = 72,
                systolic = 120,
                diastolic = 80,
                oxygenSaturation = 98,
                synced = true
            ),
            MeasurementEntity(
                id = 2,
                timestamp = Date(now - 7200000), // 2 ore fa
                systolic = 118,
                diastolic = 78,
                heartRate = 68,
                synced = true
            ),
            MeasurementEntity(
                id = 3,
                timestamp = Date(now - 10800000), // 3 ore fa
                temperature = 36.6f,
                heartRate = 70,
                synced = false
            ),
            MeasurementEntity(
                id = 4,
                timestamp = Date(now - 14400000), // 4 ore fa
                oxygenSaturation = 97,
                heartRate = 75,
                respiratoryRate = 16,
                synced = true
            ),
            MeasurementEntity(
                id = 5,
                timestamp = Date(now - 18000000), // 5 ore fa
                bloodSugar = 95f,
                heartRate = 73,
                synced = true
            )
        )
    }
}
