package ai.achaialabs.helios

import ai.achaialabs.helios.heliosApp.data.remote.datasource.ToolRemoteDataSource
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ToolRemoteDataSourceTest {

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://onzhzfcewjlhvhgmwqri.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9uemh6ZmNld2psaHZoZ213cXJpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5NTAwMDgsImV4cCI6MjA5NTUyNjAwOH0.J04xjr7Jf1dHkcSAz6A9Y_WVnHD_keFh3SlRnU9FVAc"
    ) {
        install(Postgrest)
    }

    private val remote =
        ToolRemoteDataSource(supabase)

    @Test
    fun fetchTools_shouldReturnCorrectResponse() = runTest {

        val result = remote.fetchAllTools()

        println(result)

        assertTrue(result.isSuccess)

        val tools = result.getOrNull()

        assertNotNull(tools)

        assertTrue(tools.isNotEmpty())
    }
}