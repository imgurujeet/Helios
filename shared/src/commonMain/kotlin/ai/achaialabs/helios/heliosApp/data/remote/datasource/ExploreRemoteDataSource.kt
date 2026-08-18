package ai.achaialabs.helios.heliosApp.data.remote.datasource

import ai.achaialabs.helios.heliosApp.data.remote.response.CategoryApiResponse
import ai.achaialabs.helios.heliosApp.data.remote.response.PromptApiResponse
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class ExploreRemoteDataSource(
    private val supabase: SupabaseClient,
    private val crashlytics: CrashlyticsService
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
            }.onFailure { e ->
                crashlytics.log("Failed to fetch categories")
                crashlytics.recordException(e)
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

                supabase.postgrest["prompts_with_user_state"]
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
                        filter {
                            isIn("category_id", categoryIds)
                        }
                    }
                    .decodeList<PromptApiResponse>()
            }.onFailure { e ->
                crashlytics.log("Failed to fetch prompts for categories")
                crashlytics.recordException(e)
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
            }.onFailure { e ->
                crashlytics.log("Failed to fetch prompts by category")
                crashlytics.recordException(e)
            }
        }
    }


    private val categoriesChannel =
        supabase.channel("explore-categories-realtime")

    private val promptsChannel =
        supabase.channel("explore-prompts-realtime")

    fun observeCategoryChanges(): Flow<PostgresAction> {
        return categoriesChannel
            .postgresChangeFlow<PostgresAction>(
                schema = "public"
            ) {
                table = "categories"
            }
            .onStart {
                categoriesChannel.subscribe()
            }
    }

    fun observePromptChanges(): Flow<PostgresAction> {
        return promptsChannel
            .postgresChangeFlow<PostgresAction>(
                schema = "public"
            ) {
                table = "prompts"
            }
            .onStart {
                promptsChannel.subscribe()
            }
    }
}