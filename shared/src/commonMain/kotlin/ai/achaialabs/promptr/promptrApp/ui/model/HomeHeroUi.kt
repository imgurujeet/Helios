package ai.achaialabs.promptr.promptrApp.ui.model

import ai.achaialabs.promptr.promptrApp.domain.model.HeroAction

data class HomeHeroUi(

    val id: String,

    val title: String,

    val description: String?,

    val imageUrl: String,

    val action: HeroAction
)