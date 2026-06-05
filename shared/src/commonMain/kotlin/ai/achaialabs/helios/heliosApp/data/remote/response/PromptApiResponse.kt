package ai.achaialabs.helios.heliosApp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromptApiResponse(

    val id: String,

    val title: String,
    @SerialName("prompt_text")
    val promptText: String,

    val description: String? = null,

    val tags: List<String> = emptyList(),

    @SerialName("is_liked")
    val isLiked: Boolean = false,

    @SerialName("is_bookmarked")
    val isBookmarked: Boolean = false,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("profiles")
    val author: UserApiResponse,

    @SerialName("categories")
    val category: CategoryApiResponse,

    @SerialName("media_type")
    val mediaType: String,

    @SerialName("media_url")
    val mediaUrl: String,

    @SerialName("media_thumbnail_url")
    val mediaThumbnailUrl: String? = null,

    @SerialName("media_duration_ms")
    val mediaDurationMs: Long? = null,

    @SerialName("media_aspect_ratio")
    val mediaAspectRatio: Float? = null,

    @SerialName("likes_count")
    val likesCount: Int = 0,

    @SerialName("comments_count")
    val commentsCount: Int = 0,

    @SerialName("shares_count")
    val sharesCount: Int = 0,

    @SerialName("is_premium")
    val isPremium: Boolean = false,
    @SerialName("tool")
    val tool: ToolApiResponse ?= null
)