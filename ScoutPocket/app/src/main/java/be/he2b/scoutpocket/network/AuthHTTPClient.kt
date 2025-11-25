package be.he2b.scoutpocket.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthHTTPClient {
    @Headers(
        "Content-Type: application/json",
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVhcHVhcGVkY3RlaWNocXRvamd4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg4OTY5MDgsImV4cCI6MjA3NDQ3MjkwOH0.GGtjtx3Xn-TMNn53MT-tlrEoxxv3bNCe0yN_-RCBbCo"
    )
    @POST("token?grant_type=password")
    suspend fun postAuth(
        @Body authBody: AuthBody,
    ): Response<AuthResponse>
}