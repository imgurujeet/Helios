package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeTab
import ai.achaialabs.helios.heliosApp.ui.model.HomeHeroUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.model.UserUi

data class HomeUiState(

    val isLoading: Boolean = true,
    val isPromptRefreshing: Boolean = false,// Full screen load
    val isPaginating: Boolean = false, // Small spinner at the bottom
    val prompts: List<PromptUi> = emptyList(),
    val  selectedTab: HomeTab = HomeTab.LATEST,
    val heroes: List<HomeHeroUi> = emptyList(),
    val currentUser: UserUi? = null,
    val activeVideoId: String? = null,
    val error: String? = null,
    val showAds: Boolean = true,
    val adPositions: Set<Int> = emptySet(),
    val nativeAdState: NativeAdState =
        NativeAdState.Idle,
)
