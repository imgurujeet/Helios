package ai.achaialabs.promptr.promptrApp.data.mapper

import ai.achaialabs.promptr.promptrApp.data.local.entity.PromptEntity
import ai.achaialabs.promptr.promptrApp.data.remote.dto.PromptDto
import ai.achaialabs.promptr.promptrApp.domain.model.*

/**
 * Maps DTO (from API) directly to Entity (for Database storage)
 * This is the "Retention" path.
 */
fun PromptDto.toEntity(): PromptEntity {
    return PromptEntity(
        id = id,
        title = title,
        description = description,
        authorId = author.id,
        authorName = author.name,
        authorAvatarUrl = author.avatarUrl,
        mediaType = media.type,
        mediaUrl = media.url,
        mediaThumbnailUrl = media.thumbnailUrl,
        mediaDurationMs = media.durationMs,
        mediaAspectRatio = media.aspectRatio,
        categoryId = category.id,
        categoryName = category.name,
        categoryIconUrl = category.iconUrl,
        categoryImageUrl = category.imageUrl,
        likesCount = stats.likes,
        commentsCount = stats.comments,
        sharesCount = stats.shares,
        isLiked = isLiked,
        isBookmarked = isBookmarked,
        tags = tags.joinToString(","),
        createdAt = createdAt
    )
}

/**
 * Maps DTO to Domain (if you ever need to use API data without saving to DB)
 */
fun PromptDto.toDomain(): Prompt {
    val domainMedia = if (media.type == "video") {
        FeedMedia.Video(
            id = id,
            videoUrl = media.url,
            thumbnailUrl = media.thumbnailUrl,
            durationMs = media.durationMs,
            aspectRatio = media.aspectRatio
        )
    } else {
        FeedMedia.Image(
            id = id,
            imageUrl = media.url,
            aspectRatio = media.aspectRatio
        )
    }

    return Prompt(
        id = id,
        content = PromptContent(title = title, description = description),
        author = PromptAuthor(id = author.id, name = author.name, avatarUrl = author.avatarUrl),
        media = domainMedia,
        category = PromptCategory(
            id = category.id, 
            name = category.name, 
            iconUrl = category.iconUrl, 
            imageUrl = category.imageUrl ?: ""
        ),
        stats = PromptStats(likesCount = stats.likes, commentsCount = stats.comments, sharesCount = stats.shares),
        interactions = PromptInteractions(isLiked = isLiked, isBookmarked = isBookmarked),
        metadata = PromptMetadata(tags = tags, createdAt = createdAt)
    )
}
