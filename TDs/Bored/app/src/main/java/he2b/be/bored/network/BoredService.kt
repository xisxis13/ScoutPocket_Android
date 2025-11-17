package he2b.be.bored.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object BoredService {
    private const val baseURL = "https://bored.api.lewagon.com/"
    val boredClient : BoredHttpClient

    init {
        val jsonConverter = MoshiConverterFactory.create()
        val retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(jsonConverter)
        val retrofit : Retrofit = retrofitBuilder.build()
        boredClient = retrofit.create(BoredHttpClient::class.java)
    }
}