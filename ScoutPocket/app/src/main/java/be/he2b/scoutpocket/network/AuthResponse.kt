package be.he2b.scoutpocket.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") var accessToken: String,
    @Json(name = "token_type") var tokenType: String,
    @Json(name = "expires_in") var expiresIn: Int,
    @Json(name = "expires_at") var expiresAt: Long,
    @Json(name = "refresh_token") var refreshToken: String,
    val user: Any?,
)
