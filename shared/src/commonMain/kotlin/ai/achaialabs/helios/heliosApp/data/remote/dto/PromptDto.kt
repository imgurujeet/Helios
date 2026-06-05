package ai.achaialabs.helios.heliosApp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PromptDto(
    val id: String,
    val title: String,
    val promptText: String,
    val description: String? = null,
    val author: UserDto,
    val media: MediaDto,
    val category: CategoryDto,
    val stats: StatsDto,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Long,
    @SerialName("is_premium")
    val isPremium: Boolean = false,
    val tool: ToolDto?

)

@Serializable
data class ToolDto(
    val id: String,
    val name: String,
    @SerialName("icon_url")
    val iconUrl: String? = null
)
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("is_pro")
    val isPro: Boolean = false

)

@Serializable
data class MediaDto(
    val type: String, // "image" or "video"
    val url: String,
    val thumbnailUrl: String? = null,
    val durationMs: Long? = null,
    val aspectRatio: Float? = null
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val iconUrl: String? = null,
    val imageUrl: String? = null,
    @SerialName("is_premium")
    val isPremium: Boolean = false
)

@Serializable
data class StatsDto(
    val likes: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0
)
