package com.example.fitness360.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val uid: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val lastName1: String,
    val lastName2: String,
    val actualWeight: Int,
    val goalWeight: Int,
    val height: Int,
    val age: Int,
    var gender: String,
    val activityLevel: String,
    val role: String,
    val macros: Macros
)

data class Macros(
    val carbs: Int,
    val proteins: Int,
    val fats: Int,
    val kcals: Int
)
data class RegisterResponse(val uid: String)

interface AuthService {
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}

