import retrofit2.Response
import retrofit2.http.GET
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

interface DailyRecordService {
    @GET("api/dailyRecord/getDailyRecord/{uid}/{date}")
    suspend fun getDailyRecord(
        @Path("uid") userId: String,
        @Path("date") date: String
    ): Response<DailyRecord>
}
