package com.example.fitness360.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class AddFoodRequest(
    val barcode: String,
    val uid: String,
    val type: String,
    val servingSize: Int
)

data class FoodWrapper(
    val food: FoodItem,
    val servingSize: Int
)

data class FoodItem(
    val barcode: String,
    val name: String,
    val brand: String,
    val servingSize: Float, // Puedes también cambiar este a Float si el valor puede ser decimal
    val carbs: Float,
    val proteins: Float,
    val fats: Float,
    val kcals: Float
)

data class EditFoodRequest(
    val barcode: String,
    val uid: String,
    val type: String,
    val newSize: Int
)

data class RemoveFoodRequest(
    val barcode: String,
    val uid: String,
    val type: String
)


interface MealService {
    @POST("api/meal/add-food")
    fun addFoodToMeal(@Body request: AddFoodRequest): Call<Void>


    @GET("api/meal/get-meal/{uid}/{type}")
    suspend fun getMealWithFoods(
        @Path("uid") uid: String,
        @Path("type") type: String
    ): Response<List<FoodWrapper>>


    @PUT("api/meal/edit")
    fun editFoodInMeal(@Body request: EditFoodRequest): Call<Void>

    @DELETE("api/meal/remove")
    fun deleteFoodFromMeal(
        @Query("barcode") barcode: String,
        @Query("uid") uid: String,
        @Query("type") type: String
    ): Call<Void>

    @POST("api/dailyRecord/updateNutrients/{uid}/{date}")
    fun updateProcess(
        @Path("uid") uid: String,
        @Path("date") date: String
    ): Call<Void>
}