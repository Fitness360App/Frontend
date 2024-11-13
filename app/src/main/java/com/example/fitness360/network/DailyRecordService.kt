import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
    val burnedKcals: Float
)

data class DailyRecordRequest(
    val uid: String,
    val date: String
)

interface DailyRecordService {
    @GET("api/dailyRecord/getDailyRecord/{uid}/{date}")
    suspend fun getDailyRecord(
        @Path("uid") userId: String,
        @Path("date") date: String
    ): Response<DailyRecord>

    @POST("api/dailyRecord/create")
    suspend fun createDailyRecord(
        @Body request: DailyRecordRequest
    ): Response<Void> // Usa `Void` si no necesitas datos en la respuesta, solo el código de estado
}