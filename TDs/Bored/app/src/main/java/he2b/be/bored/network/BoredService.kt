package he2b.be.bored.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object BoredService {
    private const val baseURL = "https://bored.api.lewagon.com/"
    val boredClient : BoredHTTPClient

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonConverter = MoshiConverterFactory.create(moshi)
        val retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
            .addConverterFactory(jsonConverter).baseUrl(baseURL)
        val retrofit : Retrofit = retrofitBuilder.build()
        boredClient = retrofit.create(BoredHTTPClient::class.java)
    }
}