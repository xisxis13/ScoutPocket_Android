package he2b.be.bored.network

import retrofit2.http.GET

interface BoredHTTPClient {
    @GET("api/activity")
    suspend fun getRandomActivity() : BoredResponse
}