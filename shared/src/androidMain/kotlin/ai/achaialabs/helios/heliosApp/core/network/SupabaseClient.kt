package ai.achaialabs.helios.heliosApp.core.network

import ai.achaialabs.helios.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.appleNativeLogin
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


actual fun createSupabaseClient(): SupabaseClient {
    return createSupabaseClient(
        supabaseUrl = SupabaseConfig.URL,
        supabaseKey = SupabaseConfig.ANON_KEY,
    ) {
        install(Postgrest)
        install(Auth)
        install(Storage)
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = BuildKonfig.GOOGLE_CLIENT_ID)
            appleNativeLogin()
        }
    }
}
