package ai.achaialabs.promptr.promptrApp.data.remote.mapper

import ai.achaialabs.promptr.promptrApp.data.remote.dto.HeroActionDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.HeroScheduleDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.promptr.promptrApp.data.remote.response.HomeHeroApiResponse
import kotlin.time.Instant

fun HomeHeroApiResponse.toHomeHeroDto(): HomeHeroDto {

    val schedule =
        if (startTime != null && endTime != null) {

            HeroScheduleDto(
                startTimeMillis = Instant
                    .parse(startTime)
                    .toEpochMilliseconds(),

                endTimeMillis = Instant
                    .parse(endTime)
                    .toEpochMilliseconds()
            )

        } else {
            null
        }

    return HomeHeroDto(
        id = id,

        title = title,

        description = description,

        imageUrl = imageUrl,

        action = HeroActionDto(
            type = actionType,
            value = actionValue
        ),

        schedule = schedule
    )
}