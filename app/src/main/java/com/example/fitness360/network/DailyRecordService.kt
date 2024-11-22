import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query



data class Nutrients(
    val consumedCarbs: Float,
    val consumedProteins: Float,
    val consumedFats: Float,
    val consumedKcals: Float
)

data class DailyRecord(
    val registerID: String,
    val nutrients: Nutrients,
    val steps: Float,
    val burnedKcals: Float,
    val date: String
)

data class DailyRecordRequest(
    val uid: String,
    val date: String
)

data class UpdateStepsRequest(
    val uid: String,
    val date: String,
    val steps: Int
)

data class BestStepsRecord(
    val name: String,
    val steps: Int
)


interface DailyRecordService {
    @GET("api/dailyRecord/getDailyRecord/{uid}/{date}")
    suspend fun getDailyRecord(
        @Path("uid") userId: String,
        @Path("date") date: String
    ): Response<DailyRecord>

    /*@POST("api/dailyRecord/create")
    suspend fun createDailyRecord(
        @Body request: DailyRecordRequest
    ): Response<Void> // Usa `Void` si no necesitas datos en la respuesta, solo el código de estado
    */
    @PUT("api/dailyRecord/updateSteps")
    suspend fun updateSteps(
        @Body request: UpdateStepsRequest
    ): Response<Void>

    @PUT("api/dailyRecord/updateBurnedKcalsFromSteps")
    suspend fun updateBurnedKcalsFromSteps(
        @Body request: UpdateStepsRequest
    ): Response<Void>

    @GET("api/dailyRecord/bestSteps/{date}")
    suspend fun getBestSteps(
        @Path("date") date: String
    ): Response<List<BestStepsRecord>>

    @GET("api/dailyRecord/history/{uid}/{year}/{month}")
    suspend fun getHistory(
        @Path("uid") uid: String,
        @Path("year") year: String?,
        @Path("month") month: String?
    ): Response<List<DailyRecord>>
}