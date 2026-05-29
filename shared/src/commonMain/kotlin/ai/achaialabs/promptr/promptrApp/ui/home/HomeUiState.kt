package ai.achaialabs.promptr.promptrApp.ui.home

import ai.achaialabs.promptr.promptrApp.ui.model.HomeHeroUi
import ai.achaialabs.promptr.promptrApp.ui.model.PromptUi
import ai.achaialabs.promptr.promptrApp.ui.model.UserUi

data class HomeUiState(

    val isLoading: Boolean = false,

    val prompts: List<PromptUi> = emptyList(),

    val heroes: List<HomeHeroUi> = emptyList(),

    val currentUser: UserUi? = null,

    val activeVideoId: String? = null
)