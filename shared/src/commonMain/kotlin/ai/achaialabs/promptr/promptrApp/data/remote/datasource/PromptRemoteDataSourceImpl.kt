package ai.achaialabs.promptr.promptrApp.data.remote.datasource

import ai.achaialabs.promptr.promptrApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.PromptDto
import ai.achaialabs.promptr.promptrApp.data.remote.mapper.toHomeHeroDto
import ai.achaialabs.promptr.promptrApp.data.remote.mapper.toPromptDto
import ai.achaialabs.promptr.promptrApp.data.remote.response.HomeHeroApiResponse
import ai.achaialabs.promptr.promptrApp.data.remote.response.PromptApiResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class PromptRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : PromptRemoteDataSource {

    override suspend fun getHomePrompts(): List<PromptDto> {

        return supabaseClient
            .postgrest["prompts"]
            .select(
                Columns.raw(
                    """
                    *,
                    
                    profiles:author_id(
                        id,
                        name,
                        avatar_url
                    ),
                    
                    categories:category_id(
                        id,
                        name,
                        icon_url,
                        image_url
                    )
                    """.trimIndent()
                )
            )
            .decodeList<PromptApiResponse>()
            .map { it.toPromptDto() }
    }

    override suspend fun getHomeHeroes(): List<HomeHeroDto> {

        return supabaseClient
            .postgrest["home_heroes"]
            .select()
            .decodeList<HomeHeroApiResponse>()
            .map { it.toHomeHeroDto() }
    }

    override suspend fun toggleLike(promptId: String): Boolean {

        return try {

            val currentPrompt = supabaseClient
                .postgrest["prompts"]
                .select(
                    Columns.list(
                        "is_liked",
                        "likes_count"
                    )
                ) {
                    filter {
                        eq("id", promptId)
                    }
                }
                .decodeSingle<PromptApiResponse>()

            val newLikeState = !currentPrompt.isLiked

            val newLikesCount =
                if (newLikeState) {
                    currentPrompt.likesCount + 1
                } else {
                    (currentPrompt.likesCount - 1)
                        .coerceAtLeast(0)
                }

            supabaseClient
                .postgrest["prompts"]
                .update(
                    {
                        set("is_liked", newLikeState)
                        set("likes_count", newLikesCount)
                    }
                ) {
                    filter {
                        eq("id", promptId)
                    }
                }

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun toggleBookmark(promptId: String): Boolean {

        return try {

            val currentPrompt = supabaseClient
                .postgrest["prompts"]
                .select(
                    Columns.list("is_bookmarked")
                ) {
                    filter {
                        eq("id", promptId)
                    }
                }
                .decodeSingle<PromptApiResponse>()

            val newBookmarkState =
                !currentPrompt.isBookmarked

            supabaseClient
                .postgrest["prompts"]
                .update(
                    {
                        set(
                            "is_bookmarked",
                            newBookmarkState
                        )
                    }
                ) {
                    filter {
                        eq("id", promptId)
                    }
                }

            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}