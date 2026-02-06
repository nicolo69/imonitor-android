package com.cerotek.imonitor.network.model

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val age: Int? = null,
    val pathology: String? = null
)

data class MeasurementData(
    val values: List<MeasurementValue>
)

data class MeasurementValue(
    val type: String,
    val value: Any,
    val timestamp: String
)
