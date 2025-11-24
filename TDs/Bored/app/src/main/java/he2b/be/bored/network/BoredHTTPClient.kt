package he2b.be.bored.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BoredHTTPClient {
    @GET("api/activity")
    suspend fun getRandomActivity(
        @Query("price") price : Double?,
        @Query("participants") participants: Int?,
    ) : BoredResponse
}