package com.example.fitness360.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


data class Food(
    val barcode: String,          // Código de barras del alimento
    val name: String,             // Nombre del alimento
    val brand: String,            // Marca del alimento
    val servingSize: Double,      // Tamaño de la porción (en gramos o mililitros)
    val nutrients: Nutrients      // Información nutricional
)

data class Nutrients(
    val carbs: Double,            // Carbohidratos (en gramos)
    val proteins: Double,         // Proteínas (en gramos)
    val fats: Double,             // Grasas (en gramos)
    val kcals: Double             // Calorías (en kcal)
)

interface FoodService {
    @GET("api/food/search/name/{name}")
    suspend fun searchFoodByName(@Path("name") name: String): Response<List<Food>>

    @GET("api/food/featured")
    suspend fun getFeaturedFoods(): Response<List<Food>>

    @GET("api/food/search/{barcode}")
    suspend fun searchFoodByBarcode(@Path("barcode") barcode: String): Response<Food>
}


