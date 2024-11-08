package com.example.fitness360.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


data class UserDataRequest(val uid: String)


data class UserData(
    val uid: String,
    val name: String,
    val lastName1: String,
    val lastName2: String,
    val actualWeight: Float,
    val goalWeight: Float,
    val height: Float,
    val age: Int,
    val gender: String,
    val activityLevel: String,
    val role: String,
    val carbs: Int,
    val proteins: Int,
    val fats: Int,
    val kcals: Int
)

interface UserService {
    @GET("api/users/getUserDataByID")
    suspend fun getUserDataByID(@Query("uid") userId: String): Response<UserData>
}