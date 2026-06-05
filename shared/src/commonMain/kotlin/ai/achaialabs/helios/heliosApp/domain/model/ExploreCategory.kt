package ai.achaialabs.helios.heliosApp.domain.model


// Represents the nested structure for the Explore screen
data class ExploreCategory(
    val category: PromptCategory,
    val prompts: List<Prompt>
)