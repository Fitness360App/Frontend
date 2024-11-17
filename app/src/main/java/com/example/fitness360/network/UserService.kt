package com.example.fitness360.network

import androidx.compose.ui.layout.IntrinsicMeasurable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path


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
    val kcals: Int,
    val email: String
)

data class UserSendEmailRequest(
    val uid: String,
    val password: String,
    val oldpassword: String
)

data class UserChangePasswordRequest(
    val uid: String,
    val password: String
)



data class UserDataForModification (
    val uid: String,
    val name: String,
    val lastName1: String,
    val actualWeight: Float,
    val goalWeight: Float,
    val height: Float,
    val age: Int,
    val activityLevel: String,
)

data class UserGoalsForModification (
    val uid: String,
    val proteins : Int,
    val carbs: Int,
    val fats: Int,
    val kcals: Int
)


interface UserService {
    @GET("api/users/getUserDataByID/{uid}")
    suspend fun getUserDataByID(@Path("uid") userId: String): Response<UserData>

    @DELETE("api/users/deleteUser/{uid}")
    suspend fun deleteUser(@Path("uid") userId: String): Response<Void>

    @PUT("api/users/changePassword")
    suspend fun changeUserPassword(@Body request: UserChangePasswordRequest): Response<Void>

    @PUT("api/users/sendEmailConfirmation")
    suspend fun sendEmailConfirmation(@Body request: UserSendEmailRequest): Response<String>

    @GET("api/users/checkUserEmail/{email}")
    suspend fun checkUserEmail(@Path("email") email: String): Response<Boolean>

    @PUT("api/users/modifyUserData")
    suspend fun modifyUserData(@Body updatedData: UserDataForModification): Response<Void>

    @PUT("api/users/modifyUserGoals")
    suspend fun modifyUserGoals(@Body updatedGoals: UserGoalsForModification): Response<Void>

}