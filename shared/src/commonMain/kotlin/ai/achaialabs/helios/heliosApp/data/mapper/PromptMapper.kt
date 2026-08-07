package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.*

fun PromptEntity.toDomain(): Prompt {
    val media = if (mediaType == "video") {
        FeedMedia.Video(
            id = id,
            videoUrl = mediaUrl,
            thumbnailUrl = mediaThumbnailUrl ?: "",
            durationMs = mediaDurationMs ?: 0L,
            aspectRatio = mediaAspectRatio
        )
    } else {
        FeedMedia.Image(
            id = id,
            imageUrl = mediaUrl,
            aspectRatio = mediaAspectRatio
        )
    }

    return Prompt(
        id = id,
        content = PromptContent(
            title = title,
            promptText = promptText,
            description = description
        ),
        author = PromptAuthor(
            id = authorId,
            name = authorName,
            avatarUrl = authorAvatarUrl
        ),
        media = media,
        category = PromptCategory(
            id = categoryId,
            name = categoryName,
            iconUrl = categoryIconUrl,
            imageUrl = categoryImageUrl ?: "",
            isPremium = isPremium
        ),
        stats = PromptStats(
            likesCount = likesCount,
            commentsCount = commentsCount,
            sharesCount = sharesCount
        ),
        interactions = PromptInteractions(
            isLiked = isLiked,
            isBookmarked = isBookmarked
        ),
        metadata = PromptMetadata(
            tags = tags.split(",").filter { it.isNotBlank() },
            createdAt = createdAt
        ),
        isPremium = isPremium,
        recommendedTools = Tool(
            id = tooId,
            name = tooName,
            iconUrl = tooIconUrl ?: ""
        )
    )
}

fun Prompt.toEntity(
    feedType: PromptFilter
): PromptEntity {
    return PromptEntity(
        id = id,

        title = content.title,
        promptText = content.promptText,
        description = content.description,

        authorId = author.id,
        authorName = author.name,
        authorAvatarUrl = author.avatarUrl,

        //  FIX: Use 'when' to safely extract properties from the Sealed Interface subclasses
        mediaType = when (media) {
            is FeedMedia.Image -> "image"
            is FeedMedia.Video -> "video"
        },
        mediaUrl = when (media) {
            is FeedMedia.Image -> media.imageUrl
            is FeedMedia.Video -> media.videoUrl
        },
        mediaThumbnailUrl = when (media) {
            is FeedMedia.Image -> null // Images don't have thumbnails in your model
            is FeedMedia.Video -> media.thumbnailUrl
        },
        mediaDurationMs = when (media) {
            is FeedMedia.Image -> null
            is FeedMedia.Video -> media.durationMs
        },
        mediaAspectRatio = when (media) {
            is FeedMedia.Image -> media.aspectRatio
            is FeedMedia.Video -> media.aspectRatio
        },

        categoryId = category.id,
        categoryName = category.name,
        categoryIconUrl = category.iconUrl,
        categoryImageUrl = category.imageUrl,

        likesCount = stats.likesCount,
        commentsCount = stats.commentsCount,
        sharesCount = stats.sharesCount,

        isLiked = interactions.isLiked,
        isBookmarked = interactions.isBookmarked,

        // Flatten List back to CSV string for Room
        tags = metadata.tags.joinToString(","),
        createdAt = metadata.createdAt,
        isPremium = isPremium,
        tooId = recommendedTools?.id ?: "",
        tooName = recommendedTools?.name?: "",
        tooIconUrl = recommendedTools?.iconUrl?: "",
    )
}
