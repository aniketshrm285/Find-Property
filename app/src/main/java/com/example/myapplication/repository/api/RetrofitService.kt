package com.example.myapplication.repository.api

import com.example.myapplication.models.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface RetrofitService {
    @GET("ricky1550/pariksha/db")
    suspend fun getFacilitiesAndExclusions() : Response<ApiResponse>
}