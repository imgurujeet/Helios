package ai.achaialabs.helios.heliosApp.data.remote.mapper

import ai.achaialabs.helios.heliosApp.data.remote.dto.MediaDto
import ai.achaialabs.helios.heliosApp.data.remote.dto.PromptDto
import ai.achaialabs.helios.heliosApp.data.remote.dto.StatsDto
import ai.achaialabs.helios.heliosApp.data.remote.response.PromptApiResponse
import kotlin.time.Instant

fun PromptApiResponse.toPromptDto(): PromptDto {

    return PromptDto(
        id = id,

        title = title,

        promptText = promptText,
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
            .toEpochMilliseconds(),
        isPremium = isPremium,
        tool = tool?.toDto()
    )

}