package com.cerotek.imonitor.network

import com.cerotek.imonitor.network.model.LoginRequest
import com.cerotek.imonitor.network.model.LoginResponse
import com.cerotek.imonitor.network.model.MeasurementData
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Dynamic endpoint support
    @POST
    suspend fun login(
        @Url endpoint: String,
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/measurements")
    suspend fun publishMeasurements(
        @Header("Authorization") bearer: String,
        @Header("X-Tenant") tenant: String,
        @Body data: MeasurementData
    ): Response<Void>

    @GET("api/assistees/{id}")
    suspend fun getAssistee(
        @Header("Authorization") bearer: String,
        @Path("id") assisteeId: String
    ): Response<Any>
}
