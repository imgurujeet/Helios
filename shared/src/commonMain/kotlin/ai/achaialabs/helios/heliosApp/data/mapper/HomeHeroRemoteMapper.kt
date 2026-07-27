package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.helios.heliosApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.helios.heliosApp.domain.model.HeroAction
import ai.achaialabs.helios.heliosApp.domain.model.HeroSchedule
import ai.achaialabs.helios.heliosApp.domain.model.HomeHero

/**
 * Maps DTO (API) directly to Entity (DB)
 */
fun HomeHeroDto.toEntity(): HomeHeroEntity {
    return HomeHeroEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        actionType = action.type,
        actionValue = action.value,
        startTimeMillis = schedule?.startTimeMillis,
        endTimeMillis = schedule?.endTimeMillis
    )
}

/**
 * Maps DTO to Domain
 */
fun HomeHeroDto.toDomain(): HomeHero {
    val domainAction = when (action.type) {
        "OPEN_PROMPT" -> HeroAction.OpenPrompt(action.value)
        "OPEN_CATEGORY" -> HeroAction.OpenCategory(action.value,"")
        "OPEN_URL" -> HeroAction.OpenUrl(action.value)
        else -> HeroAction.OpenUrl(action.value)
    }

    val domainSchedule = schedule?.let {
        HeroSchedule(
            startTimeMillis = it.startTimeMillis,
            endTimeMillis = it.endTimeMillis
        )
    }

    return HomeHero(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        action = domainAction,
        schedule = domainSchedule
    )
}
