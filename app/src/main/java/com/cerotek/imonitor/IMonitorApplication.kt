package com.cerotek.imonitor

import android.app.Application
import com.cerotek.imonitor.data.local.AppDatabase
import com.cerotek.imonitor.data.repository.MeasurementRepository
import com.cerotek.imonitor.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class IMonitorApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { MeasurementRepository(database.measurementDao()) }
    val apiClient by lazy { ApiClient.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: IMonitorApplication
            private set
    }
}
