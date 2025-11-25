package be.he2b.scoutpocket.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object AuthService {
    private const val BASE_URL = "https://uapuapedcteichqtojgx.supabase.co/auth/v1/"

    var authClient : AuthHTTPClient

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val jsonConverter = MoshiConverterFactory.create(moshi)
        val retrofitBuilder : Retrofit.Builder = Retrofit.Builder()
            .addConverterFactory(jsonConverter).baseUrl(BASE_URL)
        val retrofit : Retrofit = retrofitBuilder.build()
        authClient = retrofit.create(AuthHTTPClient::class.java)
    }
}