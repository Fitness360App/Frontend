package com.example.fitness360.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000" // Cambiar la linea 10 por esta variable
    private const val ipJuan = "http://192.168.1.100:3000/"
    private const val ipAca = "http://192.168.56.1:3000/"
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}