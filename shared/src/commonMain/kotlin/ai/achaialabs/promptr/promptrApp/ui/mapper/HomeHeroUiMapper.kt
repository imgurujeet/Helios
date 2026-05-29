package ai.achaialabs.promptr.promptrApp.ui.mapper

import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero
import ai.achaialabs.promptr.promptrApp.ui.model.HomeHeroUi

fun HomeHero.toUi(): HomeHeroUi {

    return HomeHeroUi(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        action = action
    )
}