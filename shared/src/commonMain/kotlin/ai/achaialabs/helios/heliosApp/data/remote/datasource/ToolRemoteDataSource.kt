package ai.achaialabs.helios.heliosApp.data.remote.datasource

import ai.achaialabs.helios.heliosApp.data.remote.response.ToolApiResponse
import androidx.compose.foundation.layout.Column
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ToolRemoteDataSource (
    private val supabase: SupabaseClient
){
    suspend fun fetchAllTools() : Result<List<ToolApiResponse>>{
        return withContext(Dispatchers.IO){
            runCatching {
                supabase.postgrest["tool"]
                    .select(Columns.ALL)
                    .decodeList<ToolApiResponse>()

            }
        }
    }
}