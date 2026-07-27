package ai.achaialabs.helios.heliosApp.data.remote.datasource

import ai.achaialabs.helios.heliosApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.helios.heliosApp.data.remote.dto.PromptDto
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toHomeHeroDto
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toPromptDto
import ai.achaialabs.helios.heliosApp.data.remote.response.HomeHeroApiResponse
import ai.achaialabs.helios.heliosApp.data.remote.response.PromptApiResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order // Added import for ordering
import io.github.jan.supabase.postgrest.rpc

class PromptRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : PromptRemoteDataSource {

    override suspend fun getHomePrompts(page: Int, pageSize: Int): List<PromptDto> {
        val fromIndex = (page * pageSize).toLong()
        val toIndex = fromIndex + pageSize - 1


        val apiResponse = supabaseClient
                .postgrest["prompts_with_user_state"]
            .select(
                Columns.raw(
                    """
                *,
                profiles:author_id(id, name, avatar_url),
                categories:category_id(id, name, icon_url, image_url),
                tool(*)
                """.trimIndent()
                )
            ) {
                range(fromIndex, toIndex)
                order("created_at", Order.DESCENDING)
            }
            .decodeList<PromptApiResponse>()




        // 3. Finally, map it and return it
        return apiResponse.map { it.toPromptDto() }


    }


    override suspend fun searchPrompts(query: String): List<PromptDto> {

        val apiResponse = supabaseClient
            .postgrest["prompts_with_user_state"]
            .select(
                Columns.raw(
                    """
                *,
                profiles:author_id(id, name, avatar_url),
                categories:category_id(id, name, icon_url, image_url),
                tool(*)
                """.trimIndent()
                )
            ) {
                filter {
                    ilike("title", "%$query%")
                }
                order("created_at", Order.DESCENDING)
                limit(50)
            }
            .decodeList<PromptApiResponse>()

        return apiResponse.map { it.toPromptDto() }
    }


    override suspend fun getHomeHeroes(): List<HomeHeroDto> {
        return supabaseClient
            .postgrest["home_heroes"]
            .select()
            .decodeList<HomeHeroApiResponse>()
            .map { it.toHomeHeroDto() }
    }

    //  SCALABLE APPROACH: Let the Database handle the logic atomically
    override suspend fun toggleLike(promptId: String): Boolean {
        return try {
            supabaseClient.postgrest.rpc(
                function = "toggle_prompt_like",
                parameters = mapOf("p_prompt_id" to promptId)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //  SCALABLE APPROACH: Let the Database handle the logic atomically
    override suspend fun toggleBookmark(promptId: String): Boolean {
        return try {
            supabaseClient.postgrest.rpc(
                function = "toggle_prompt_bookmark",
                parameters = mapOf("p_prompt_id" to promptId)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}