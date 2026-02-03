package be.he2b.scoutpocket.network

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {

    private const val SUPABASE_URL = "https://kddjxaeanuvuecnnpujt.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_5nAnrJctcg28AZUBMeOUzg_snhIF8aG"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY,
    ) {
        install(Auth)

        install(Postgrest)

        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
        })
    }

}