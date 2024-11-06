package com.example.fitness360.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

data class DailyRecordDataRequest(val uid: String)


data class DailyRecord(

    val registerID: String,
    val consumedCarbs: String,
    val consumedProteins: String,
    val consumedFats: String,
    val consumedKcals: Float,
    val steps: Float,
    val burnedKcals: Float,

)

interface DailyRecordService {
    @GET("api/dailyRecord/getdailyRecord")
    fun getDailyRecord(
        @Query("uid") userId: String,
        @Query("date") date: String
    ): Call<DailyRecord>
}