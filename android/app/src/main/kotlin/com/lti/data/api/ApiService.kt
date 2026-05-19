package com.lti.data.api

import com.lti.data.model.HealthResponse
import retrofit2.http.GET

interface ApiService {
    @GET("health")
    suspend fun getHealth(): HealthResponse
}
