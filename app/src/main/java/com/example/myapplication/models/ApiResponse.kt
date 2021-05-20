package com.example.myapplication.models


import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val exclusions: List<List<Exclusion>>,
    val facilities: List<Facility>
)