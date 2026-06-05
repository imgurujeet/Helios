package ai.achaialabs.helios.heliosApp.ui.mapper

import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.ui.model.HomeHeroUi

fun HomeHero.toUi(): HomeHeroUi {

    return HomeHeroUi(
        id = id,
        title = title,
        description = description,
        imageUrl = imageUrl,
        action = action
    )
}