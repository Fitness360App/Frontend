package com.example.fitness360.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class AddFoodRequest(
    val barcode: String,
    val uid: String,
    val type: String
)

interface MealService {
    @POST("api/meal/add-food")
    fun addFoodToMeal(@Body request: AddFoodRequest): Call<Void>
}