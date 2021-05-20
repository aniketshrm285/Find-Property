package com.example.myapplication.repository

import com.example.myapplication.repository.api.RetrofitService

class PropertyRepository(
    private val retrofit: RetrofitService
) {
    suspend fun getFacilitiesAndExclusions() = retrofit.getFacilitiesAndExclusions()
}