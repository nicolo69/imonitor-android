package com.cerotek.imonitor.ble

import android.content.Context
import android.util.Log
import com.cerotek.imonitor.ble.model.AllDataBean
import com.cerotek.imonitor.ble.model.WatchState
import com.cerotek.imonitor.util.WatchBatteryManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WatchMonitor(private val context: Context) {

    private val _watchState = MutableStateFlow(0)
    val watchState: StateFlow<Int> = _watchState.asStateFlow()

    private val _measurementData = MutableStateFlow<List<AllDataBean>>(emptyList())
    val measurementData: StateFlow<List<AllDataBean>> = _measurementData.asStateFlow()
    
    private val batteryManager = WatchBatteryManager(context)
    
    fun batteryLevel(): StateFlow<Int> = batteryManager.batteryLevel
    fun isCharging(): StateFlow<Boolean> = batteryManager.isCharging

    var watchMacAddress: String = ""
        set(value) {
            if (field != value) {
                field = value
                scan()
            }
        }

    var measurePeriodMinutes: Int = 5
    var calibrationParams: CalibrationParams? = null

    private var lastRetrieveDataTs: Long = 0
    private var previousBatteryLevel: Int = -1
    private var isRunning: Boolean = false

    data class CalibrationParams(
        val systolic: Int,
        val diastolic: Int,
        val glycemia: Float = 0f,
        val cholesterol: Float = 0f
    )

    fun init() {
        Log.i(TAG, "Initializing WatchMonitor")
        // YCBTClient.initClient(context, false)
        // Initialize BLE SDK here
        // YCBTClient.deviceToApp(bleDeviceToAppDataResponse)
        // YCBTClient.registerBleStateChange(bleConnectResponse)
        // YCBTClient.setReconnect(true)
    }

    fun deInit() {
        Log.i(TAG, "Deinitializing WatchMonitor")
        batteryManager.setWatchConnected(false)
        // YCBTClient.registerBleStateChange(null)
        // YCBTClient.deviceToApp(null)
        // YCBTClient.disconnectBle()
    }

    fun scan() {
        Log.i(TAG, "Starting BLE scan")
        disableState(WatchState.WATCH_FOUND)
        
        // YCBTClient.startScanBle({ code, device ->
        //     device?.let {
        //         Log.i(TAG, "Found device: ${it.deviceMac}")
        //         if (it.deviceMac.replace(":", "") == watchMacAddress) {
        //             connect(it.deviceMac)
        //             enableState(WatchState.WATCH_FOUND)
        //             YCBTClient.stopScanBle()
        //         }
        //     }
        // }, 10)
    }

    fun connect(macAddress: String) {
        Log.i(TAG, "Connecting to device: $macAddress")
        batteryManager.setWatchConnected(true)
        // YCBTClient.connectBle(macAddress) { code ->
        //     if (code == BLEState.ReadWriteOK) {
        //         setupMonitor()
        //     }
        // }
    }
    
    fun disconnect() {
        Log.i(TAG, "Disconnecting from device")
        batteryManager.setWatchConnected(false)
        // YCBTClient.disconnectBle()
        disableState(WatchState.WATCH_CONNECTED)
        disableState(WatchState.WATCH_FOUND)
    }

    private fun setupMonitor() {
        Log.i(TAG, "Setting up watch monitor")
        isRunning = false
        setupDeviceSettings()
    }

    private fun setupDeviceSettings() {
        // Set language to Italian (0x07)
        // YCBTClient.settingLanguage(0x07) { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "Language set to Italian")
        //         setupHeartMonitor()
        //     }
        // }
    }

    private fun setupHeartMonitor() {
        // YCBTClient.settingHeartMonitor(0x01, measurePeriodMinutes) { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "Heart monitor configured: $measurePeriodMinutes min interval")
        //         setupTemperatureMonitor()
        //     }
        // }
    }

    private fun setupTemperatureMonitor() {
        // YCBTClient.settingTemperatureMonitor(true, measurePeriodMinutes) { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "Temperature monitor configured")
        //         setupBloodOxygenMonitor()
        //     }
        // }
    }

    private fun setupBloodOxygenMonitor() {
        // YCBTClient.settingBloodOxygenModeMonitor(true, measurePeriodMinutes) { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "Blood oxygen monitor configured")
        //         setupPpgCollect()
        //     }
        // }
    }

    private fun setupPpgCollect() {
        // YCBTClient.settingDataCollect(0x01, 0, 60, measurePeriodMinutes) { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "PPG data collection configured")
        //         setPhoneTime()
        //     }
        // }
    }

    private fun setPhoneTime() {
        // YCBTClient.settingTime { code, _, _ ->
        //     if (code == 0) {
        //         Log.i(TAG, "Time synchronized")
        //         isRunning = true
        //         calibrationParams?.let { performCalibration(it) }
        //     }
        // }
    }

    private fun performCalibration(params: CalibrationParams) {
        // Blood pressure calibration
        // YCBTClient.appBloodCalibration(params.systolic, params.diastolic) { _, _, _ ->
        //     Log.i(TAG, "Blood pressure calibrated")
        //     
        //     if (params.glycemia > 0) {
        //         val glycemiaInt = params.glycemia.toInt()
        //         val glycemiaFrac = ((params.glycemia - glycemiaInt) * 10).toInt()
        //         // YCBTClient.appBloodSugarCalibration(glycemiaInt, glycemiaFrac, 0) { _, _, _ ->
        //         //     Log.i(TAG, "Glycemia calibrated")
        //         // }
        //     }
        // }
    }

    fun retrieveHealthHistoryData() {
        if (!isRunning) {
            Log.e(TAG, "WatchMonitor not running, cannot retrieve data")
            return
        }

        // YCBTClient.healthHistoryData(Constants.DATATYPE.Health_HistoryAll) { code, ratio, resultMap ->
        //     if (code == 0 && resultMap != null) {
        //         val response = Gson().fromJson(resultMap.toString(), AllDataResponse::class.java)
        //         val allData = response.data
        //         
        //         if (allData.isNotEmpty()) {
        //             disableState(WatchState.WATCH_NOT_WEAR)
        //             _measurementData.value = allData
        //             lastRetrieveDataTs = System.currentTimeMillis()
        //         } else {
        //             checkWearStatus()
        //         }
        //     }
        // }
    }

    private fun checkWearStatus() {
        val now = System.currentTimeMillis()
        val threshold = lastRetrieveDataTs + (measurePeriodMinutes * 60_000 * 1.5)
        
        if (now > threshold) {
            Log.e(TAG, "Watch seems not worn")
            enableState(WatchState.WATCH_NOT_WEAR)
        }
    }

    fun checkBattery() {
        // YCBTClient.getDeviceInfo { code, _, hashMap ->
        //     if (code == 0) {
        //         val info = Gson().fromJson(hashMap.toString(), BandBaseInfo::class.java)
        //         val batteryLevel = info.data.deviceBatteryValue.toInt()
        //         
        //         handleBatteryLevel(batteryLevel)
        //     }
        // }
    }

    private fun handleBatteryLevel(level: Int) {
        Log.i(TAG, "Battery level: $level%")
        
        disableState(WatchState.WATCH_BATTERY_LOW)
        
        val isCharging = previousBatteryLevel != -1 && level > previousBatteryLevel
        
        // Aggiorna il battery manager
        batteryManager.updateBatteryLevel(level, isCharging)
        
        when {
            isCharging -> {
                enableState(WatchState.WATCH_RECHARGING)
            }
            isStateEnabled(WatchState.WATCH_RECHARGING) && level < previousBatteryLevel -> {
                disableState(WatchState.WATCH_RECHARGING)
            }
            level < 20 -> {
                enableState(WatchState.WATCH_BATTERY_LOW)
            }
        }
        
        previousBatteryLevel = level
    }

    fun deleteHistoricalMeasures() {
        // YCBTClient.deleteHealthHistoryData(Constants.DATATYPE.Health_DeleteAll) { code, _, _ ->
        //     if (code != 0) {
        //         Log.e(TAG, "Failed to delete historical measures")
        //     }
        // }
    }

    private fun enableState(flag: Int) {
        _watchState.value = _watchState.value or flag
        Log.i(TAG, "Watch state: ${_watchState.value.toString(2).padStart(16, '0')}")
    }

    private fun disableState(flag: Int) {
        _watchState.value = _watchState.value and flag.inv()
        Log.i(TAG, "Watch state: ${_watchState.value.toString(2).padStart(16, '0')}")
    }

    fun isStateEnabled(flag: Int): Boolean {
        return (_watchState.value and flag) != 0
    }

    companion object {
        private const val TAG = "WatchMonitor"
        
        fun isStateEnabled(state: Int, flag: Int): Boolean {
            return (state and flag) != 0
        }
    }
}
