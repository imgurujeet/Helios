package ai.achaialabs.helios.heliosApp.data.remote.datasource

import ai.achaialabs.helios.heliosApp.data.remote.response.CategoryApiResponse
import ai.achaialabs.helios.heliosApp.data.remote.response.PromptApiResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ExploreRemoteDataSource(
    private val supabase: SupabaseClient
) {
    /**
     * Fetches a paginated list of categories from the Supabase "categories" table.
     */
    suspend fun fetchCategories(limit: Int, offset: Int): Result<List<CategoryApiResponse>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                supabase.postgrest["categories"] // Name of your Supabase table
                    .select(columns = Columns.ALL) {
                        limit(count = limit.toLong())
                        range(from = offset.toLong(), to = (offset + limit - 1).toLong())
                    }
                    .decodeList<CategoryApiResponse>() // Decodes directly into your ApiResponse model
            }
        }
    }

    /**
     * Fetches prompts that belong to a specific list of categories.
     */
    suspend fun fetchPromptsForCategories(categoryIds: List<String>): Result<List<PromptApiResponse>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                if (categoryIds.isEmpty()) return@runCatching emptyList()

                supabase.postgrest["prompts_with_user_state"] // Name of your Supabase table
                    .select(columns = Columns.ALL) {
                        // Filters the query to only bring back prompts for the categories we just downloaded!
                        filter {
                            PromptApiResponse::category isIn categoryIds
                        }
                    }
                    .decodeList<PromptApiResponse>()
            }
        }
    }

    /**
     * Fetches a paginated list of prompts for a specific category.
     */
    suspend fun fetchPromptsByCategory(
        categoryId: String,
        limit: Int,
        offset: Int
    ): Result<List<PromptApiResponse>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                supabase.postgrest["prompts_with_user_state"] // Assuming this is your prompts table
                    .select(
                        columns = Columns.raw(
                            """
                        *,
                        profiles:author_id(id, name, avatar_url),
                        categories:category_id(id, name, icon_url, image_url),
                        tool(*)
                        """.trimIndent()
                        )
                    ) {
                        // Filter by the specific category
                        filter {
                            eq("category_id", categoryId)
                        }
                        // Apply pagination
                        range(from = offset.toLong(), to = (offset + limit - 1).toLong())
                        // Sort by newest first
                        order("created_at", Order.DESCENDING)
                    }
                    .decodeList<PromptApiResponse>()
            }
        }
    }
}