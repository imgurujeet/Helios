package ai.achaialabs.promptr.promptrApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val mediaType: String, // "image" or "video"
    val mediaUrl: String,
    val mediaThumbnailUrl: String?,
    val mediaDurationMs: Long?,
    val mediaAspectRatio: Float?,
    val categoryId: String,
    val categoryName: String,
    val categoryIconUrl: String?,
    val categoryImageUrl: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val tags: String, // Comma separated
    val createdAt: Long
)
