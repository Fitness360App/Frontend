package com.example.fitness360.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class AddFoodRequest(
    val barcode: String,
    val uid: String,
    val type: String
)

data class FoodItem(
    val barcode: String,
    val name: String,
    val brand: String,
    val servingSize: Int,
    val carbs: Int,
    val proteins: Int,
    val fats: Int,
    val kcals: Int
)


interface MealService {
    @POST("api/meal/add-food")
    fun addFoodToMeal(@Body request: AddFoodRequest): Call<Void>


    @GET("api/meal/get-meal/{uid}/{type}")
    suspend fun getMealWithFoods(
        @Path("uid") uid: String,
        @Path("type") type: String
    ): Response<List<FoodItem>>
}