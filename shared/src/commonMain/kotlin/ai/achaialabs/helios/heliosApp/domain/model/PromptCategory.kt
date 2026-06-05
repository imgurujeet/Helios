package ai.achaialabs.helios.heliosApp.domain.model

data class PromptCategory(

    val id: String,

    val name: String,

    val iconUrl: String? = null,

    val imageUrl: String? = null,
    val isPremium: Boolean
)