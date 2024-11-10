package com.example.fitness360.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


data class IMCResponse(
    val imc: Float // Ajusta este campo según el tipo de dato que devuelva la API
)

interface CalculatorService {
    @GET("api/calculator/calculateIMC/{uid}")
    suspend fun calculateIMCByUserData(
        @Path("uid") uid: String
    ): Response<IMCResponse>
}
