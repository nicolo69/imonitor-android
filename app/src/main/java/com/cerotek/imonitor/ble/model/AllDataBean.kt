package com.cerotek.imonitor.ble.model

import com.google.gson.annotations.SerializedName

data class AllDataResponse(
    val data: List<AllDataBean>
)

data class AllDataBean(
    @SerializedName("DBPValue")
    val diastolic: Int,
    
    @SerializedName("OOValue")
    val oxygenSaturation: Int,
    
    @SerializedName("SBPValue")
    val systolic: Int,
    
    @SerializedName("bloodSugarValue")
    val bloodSugarValue: Int,
    
    @SerializedName("bodyFatFloatValue")
    val bodyFatFloat: Int,
    
    @SerializedName("bodyFatIntValue")
    val bodyFatInt: Int,
    
    @SerializedName("cvrrValue")
    val cvrr: Int,
    
    @SerializedName("heartValue")
    val heartRate: Int,
    
    @SerializedName("hrvValue")
    val hrv: Int,
    
    @SerializedName("respiratoryRateValue")
    val respiratoryRate: Int,
    
    @SerializedName("startTime")
    val timestamp: Long,
    
    @SerializedName("stepValue")
    val steps: Int,
    
    @SerializedName("tempFloatValue")
    val tempFloat: Int,
    
    @SerializedName("tempIntValue")
    val tempInt: Int
) {
    val bloodSugarFloat: Float
        get() = bloodSugarValue / 10f
    
    val bodyTemperature: Float
        get() = tempInt + (tempFloat / 10f)
}

