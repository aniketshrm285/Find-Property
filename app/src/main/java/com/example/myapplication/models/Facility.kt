package com.example.myapplication.models


import com.google.gson.annotations.SerializedName

data class Facility(
    @SerializedName("facility_id")
    val facilityId: String,
    val name: String,
    val options: List<Option>
)