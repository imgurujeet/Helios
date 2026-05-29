package ai.achaialabs.promptr.promptrApp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeHeroDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val imageUrl: String,
    val action: HeroActionDto,
    val schedule: HeroScheduleDto? = null
)

@Serializable
data class HeroActionDto(
    val type: String, // "OPEN_PROMPT", "OPEN_CATEGORY", "OPEN_URL"
    val value: String
)

@Serializable
data class HeroScheduleDto(
    val startTimeMillis: Long,
    val endTimeMillis: Long
)
