package com.cerotek.imonitor.data.model

data class ParameterThreshold(
    val parameterType: String,
    val minValue: Float,
    val maxValue: Float,
    val unit: String
)

object ParameterTypes {
    const val HEART_RATE = "heart_rate"
    const val BLOOD_PRESSURE_SYSTOLIC = "blood_pressure_systolic"
    const val BLOOD_PRESSURE_DIASTOLIC = "blood_pressure_diastolic"
    const val OXYGEN_SATURATION = "oxygen_saturation"
    const val TEMPERATURE = "temperature"
    const val BLOOD_SUGAR = "blood_sugar"
    const val BODY_FAT = "body_fat"
}

object DefaultThresholds {
    val HEART_RATE = ParameterThreshold(ParameterTypes.HEART_RATE, 60f, 100f, "bpm")
    val BLOOD_PRESSURE_SYSTOLIC = ParameterThreshold(ParameterTypes.BLOOD_PRESSURE_SYSTOLIC, 90f, 140f, "mmHg")
    val BLOOD_PRESSURE_DIASTOLIC = ParameterThreshold(ParameterTypes.BLOOD_PRESSURE_DIASTOLIC, 60f, 90f, "mmHg")
    val OXYGEN = ParameterThreshold(ParameterTypes.OXYGEN_SATURATION, 95f, 100f, "%")
    val TEMPERATURE = ParameterThreshold(ParameterTypes.TEMPERATURE, 36.0f, 37.5f, "°C")
    val GLUCOSE = ParameterThreshold(ParameterTypes.BLOOD_SUGAR, 70f, 140f, "mg/dL")
    val BODY_FAT = ParameterThreshold(ParameterTypes.BODY_FAT, 10f, 30f, "%")
    
    fun getAll(): List<ParameterThreshold> {
        return listOf(
            HEART_RATE,
            BLOOD_PRESSURE_SYSTOLIC,
            BLOOD_PRESSURE_DIASTOLIC,
            OXYGEN,
            TEMPERATURE,
            GLUCOSE,
            BODY_FAT
        )
    }
}
