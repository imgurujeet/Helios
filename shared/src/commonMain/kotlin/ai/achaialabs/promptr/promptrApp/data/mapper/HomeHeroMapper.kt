package ai.achaialabs.promptr.promptrApp.data.mapper

import ai.achaialabs.promptr.promptrApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.promptr.promptrApp.domain.model.HeroAction
import ai.achaialabs.promptr.promptrApp.domain.model.HeroSchedule
import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero

fun HomeHeroEntity.toDomain(): HomeHero {
    val action = when (actionType.uppercase()) {
        "OPEN_PROMPT" -> HeroAction.OpenPrompt(actionValue)
        "OPEN_CATEGORY" -> HeroAction.OpenCategory(actionValue)
        "OPEN_URL" -> HeroAction.OpenUrl(actionValue)
        else -> HeroAction.OpenUrl(actionValue)
    }

    val schedule = if (startTimeMillis != null && endTimeMillis != null) {
        HeroSchedule(startTimeMillis, endTimeMillis)
    } else null

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
    val actionType: String
    val actionValue: String

    when (val a = action) {
        is HeroAction.OpenCategory -> {
            actionType = "OpenCategory"
            actionValue = a.categoryId
        }
        is HeroAction.OpenPrompt -> {
            actionType = "OpenPrompt"
            actionValue = a.promptId
        }
        is HeroAction.OpenUrl -> {
            actionType = "OpenUrl"
            actionValue = a.url
        }
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
