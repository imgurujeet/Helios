package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.helios.heliosApp.domain.model.HeroAction
import ai.achaialabs.helios.heliosApp.domain.model.HeroSchedule
import ai.achaialabs.helios.heliosApp.domain.model.HomeHero

fun HomeHeroEntity.toDomain(): HomeHero {

    val action = when (actionType.uppercase()) {

        "OPEN_PROMPT" ->
            HeroAction.OpenPrompt(actionValue)

        "OPEN_CATEGORY" ->
            HeroAction.OpenCategory(actionValue,categoryName=title)

        "OPEN_URL" ->
            HeroAction.OpenUrl(actionValue)

        "OPEN_SEARCH" ->
            HeroAction.OpenSearch(actionValue)

        "OPEN_SCREEN" ->
            HeroAction.OpenScreen(actionValue)

        else ->
            HeroAction.None
    }

    val schedule =
        if (startTimeMillis != null && endTimeMillis != null) {
            HeroSchedule(startTimeMillis, endTimeMillis)
        } else {
            null
        }

    return HomeHero(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        action = action,
        schedule = schedule
    )
}

fun HomeHero.toEntity(): HomeHeroEntity {

    val (actionType, actionValue) = when (val a = action) {

        is HeroAction.OpenPrompt ->
            "OPEN_PROMPT" to a.promptId

        is HeroAction.OpenCategory ->
            "OPEN_CATEGORY" to a.categoryId

        is HeroAction.OpenUrl ->
            "OPEN_URL" to a.url

        is HeroAction.OpenSearch ->
            "OPEN_SEARCH" to a.query

        is HeroAction.OpenScreen ->
            "OPEN_SCREEN" to a.screen

        HeroAction.None ->
            "NONE" to ""
    }

    return HomeHeroEntity(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        actionType = actionType,
        actionValue = actionValue,
        startTimeMillis = schedule?.startTimeMillis,
        endTimeMillis = schedule?.endTimeMillis
    )
}