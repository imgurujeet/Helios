package ai.achaialabs.helios.heliosApp.ui.model

import ai.achaialabs.helios.heliosApp.domain.model.HeroAction

data class HomeHeroUi(

    val id: String,

    val title: String,

    val description: String?,

    val imageUrl: String,

    val action: HeroAction
)