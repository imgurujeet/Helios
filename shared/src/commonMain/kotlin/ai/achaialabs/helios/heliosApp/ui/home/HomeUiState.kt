package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.ui.model.HomeHeroUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.model.UserUi

data class HomeUiState(
    val isLoading: Boolean = true,     // Full screen load
    val isPaginating: Boolean = false, // Small spinner at the bottom
    val prompts: List<PromptUi> = emptyList(),
    val heroes: List<HomeHeroUi> = emptyList(),
    val currentUser: UserUi? = null,
    val activeVideoId: String? = null,
    val error: String? = null
)
