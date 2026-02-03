package be.he2b.scoutpocket.network

import android.content.Context
import android.content.SharedPreferences
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SupabaseClient {

    private const val SUPABASE_URL = "https://kddjxaeanuvuecnnpujt.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_5nAnrJctcg28AZUBMeOUzg_snhIF8aG"

    lateinit var client: SupabaseClient

    fun initialize(context: Context) {
        if (::client.isInitialized) return

        client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY,
        ) {
            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
            })

            install(Postgrest)
            install(Auth) {
                sessionManager = AndroidSessionManager(context)
            }
        }
    }

}

class AndroidSessionManager(context: Context) : SessionManager {
    private val prefs: SharedPreferences = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

    override suspend fun saveSession(session: UserSession) {
        prefs.edit().putString("session", Json.encodeToString(session)).apply()
    }

    override suspend fun loadSession(): UserSession? {
        val json = prefs.getString("session", null) ?: return null
        return try {
            Json.decodeFromString(json)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteSession() {
        prefs.edit().remove("session").apply()
    }
}