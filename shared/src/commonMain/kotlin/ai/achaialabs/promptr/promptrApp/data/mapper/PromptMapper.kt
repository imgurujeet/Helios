package ai.achaialabs.promptr.promptrApp.data.mapper

import ai.achaialabs.promptr.promptrApp.data.local.entity.PromptEntity
import ai.achaialabs.promptr.promptrApp.domain.model.*

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
            imageUrl = categoryImageUrl ?: ""
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
        )
    )
}

fun Prompt.toEntity(): PromptEntity {
    val mediaType: String
    val mediaUrl: String
    val mediaThumbnailUrl: String?
    val mediaDurationMs: Long?
    val mediaAspectRatio: Float?

    when (val m = media) {
        is FeedMedia.Image -> {
            mediaType = "image"
            mediaUrl = m.imageUrl
            mediaThumbnailUrl = null
            mediaDurationMs = null
            mediaAspectRatio = m.aspectRatio
        }
        is FeedMedia.Video -> {
            mediaType = "video"
            mediaUrl = m.videoUrl
            mediaThumbnailUrl = m.thumbnailUrl
            mediaDurationMs = m.durationMs
            mediaAspectRatio = m.aspectRatio
        }
    }

    return PromptEntity(
        id = id,
        title = content.title,
        description = content.description,
        authorId = author.id,
        authorName = author.name,
        authorAvatarUrl = author.avatarUrl,
        mediaType = mediaType,
        mediaUrl = mediaUrl,
        mediaThumbnailUrl = mediaThumbnailUrl,
        mediaDurationMs = mediaDurationMs,
        mediaAspectRatio = mediaAspectRatio,
        categoryId = category.id,
        categoryName = category.name,
        categoryIconUrl = category.iconUrl,
        categoryImageUrl = category.imageUrl,
        likesCount = stats.likesCount,
        commentsCount = stats.commentsCount,
        sharesCount = stats.sharesCount,
        isLiked = interactions.isLiked,
        isBookmarked = interactions.isBookmarked,
        tags = metadata.tags.joinToString(","),
        createdAt = metadata.createdAt
    )
}
