package ai.achaialabs.promptr.promptrApp.data.remote.mapper

import ai.achaialabs.promptr.promptrApp.data.remote.dto.MediaDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.PromptDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.StatsDto
import ai.achaialabs.promptr.promptrApp.data.remote.response.PromptApiResponse
import kotlin.time.Instant

fun PromptApiResponse.toPromptDto(): PromptDto {

    return PromptDto(
        id = id,

        title = title,

        description = description,

        author = author.toDto(),

        media = MediaDto(
            type = mediaType,
            url = mediaUrl,
            thumbnailUrl = mediaThumbnailUrl,
            durationMs = mediaDurationMs,
            aspectRatio = mediaAspectRatio
        ),

        category = category.toDto(),

        stats = StatsDto(
            likes = likesCount,
            comments = commentsCount,
            shares = sharesCount
        ),

        isLiked = isLiked,

        isBookmarked = isBookmarked,

        tags = tags,

        createdAt = Instant
            .parse(createdAt)
            .toEpochMilliseconds()
    )
}