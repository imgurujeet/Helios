package ai.achaialabs.helios.heliosApp.domain.model

sealed interface HeroAction {

    data class OpenPrompt(
        val promptId: String
    ) : HeroAction

    data class OpenCategory(
        val categoryId: String,
        val categoryName: String
    ) : HeroAction

    data class OpenUrl(
        val url: String
    ) : HeroAction

    data class OpenSearch(
        val query: String
    ) : HeroAction

    data class OpenScreen(
        val screen: String
    ) : HeroAction

    data object None : HeroAction
}