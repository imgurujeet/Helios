package ai.achaialabs.helios.heliosApp.domain.model

sealed interface HeroAction {

    data class OpenPrompt(
        val promptId: String
    ) : HeroAction

    data class OpenCategory(
        val categoryId: String
    ) : HeroAction

    data class OpenUrl(
        val url: String
    ) : HeroAction
}