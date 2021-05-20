package com.example.myapplication.models


import com.google.gson.annotations.SerializedName

data class Exclusion(
    @SerializedName("facility_id")
    val facilityId: String,
    @SerializedName("options_id")
    val optionsId: String
)