package com.cerotek.imonitor.data.model

data class ParameterThreshold(
    val parameterType: String,
    val minValue: Float,
    val maxValue: Float,
    val unit: String
)

object DefaultThresholds {
    val HEART_RATE = ParameterThreshold("heart_rate", 60f, 100f, "bpm")
    val BLOOD_PRESSURE_SYSTOLIC = ParameterThreshold("blood_pressure_sys", 90f, 140f, "mmHg")
    val BLOOD_PRESSURE_DIASTOLIC = ParameterThreshold("blood_pressure_dia", 60f, 90f, "mmHg")
    val OXYGEN = ParameterThreshold("oxygen", 95f, 100f, "%")
    val TEMPERATURE = ParameterThreshold("temperature", 36.0f, 37.5f, "°C")
    val GLUCOSE = ParameterThreshold("glucose", 70f, 140f, "mg/dL")
    val SATURATION = ParameterThreshold("saturation", 94f, 100f, "%")
    
    fun getAll(): List<ParameterThreshold> {
        return listOf(
            HEART_RATE,
            BLOOD_PRESSURE_SYSTOLIC,
            BLOOD_PRESSURE_DIASTOLIC,
            OXYGEN,
            TEMPERATURE,
            GLUCOSE,
            SATURATION
        )
    }
}
