package com.example.myapplication.repository.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://my-json-server.typicode.com/"
object RetrofitClient {
    private var client : RetrofitService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RetrofitService::class.java)

    val getClient:RetrofitService
    get() = client
}