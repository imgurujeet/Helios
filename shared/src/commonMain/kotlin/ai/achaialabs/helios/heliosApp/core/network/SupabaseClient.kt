package ai.achaialabs.helios.heliosApp.core.network

import io.github.jan.supabase.SupabaseClient
import ai.achaialabs.helios.BuildKonfig

expect fun createSupabaseClient(): SupabaseClient

object SupabaseConfig {
     val URL = BuildKonfig.SUPABASE_URL
     val ANON_KEY = BuildKonfig.SUPABASE_KEY

}
