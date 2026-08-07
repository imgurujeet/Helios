package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomeHeroesUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.GetPremiumStatusUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.GetRemixPromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.RefreshHomeDataUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleBookmarkUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.SyncHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.firebase.Inappmessaging.InAppMessagingService
import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import ai.achaialabs.helios.heliosApp.ui.home.components.HomeTab
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import ai.achaialabs.helios.heliosApp.ui.model.HomeHeroUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomePromptsUseCase: GetHomePromptsUseCase,
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val refreshHomeDataUseCase: RefreshHomeDataUseCase,
    private val syncHomePromptsUseCase: SyncHomePromptsUseCase, // Added for pagination!
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getRemixPromptsUseCase: GetRemixPromptsUseCase,
    private val adManager: AdManager,
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val crashlytics: CrashlyticsService,
    private val analytics: AnalyticsService,
    private val inAppMessagingService: InAppMessagingService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()


    // --- Pagination Trackers ---
    private val _currentLimit = MutableStateFlow(20) // Initial limit
    private var currentPage = 0
    private var isSyncing = false
    private var hasReachedEnd = false
    private val adPositions = mutableSetOf<Int>()


    val isPremium: StateFlow<Boolean> = getPremiumStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observePremiumStatus()
        setupOfflineFirstObservation()
        setupHeroesAndUser()
        observeNativeAds()

        // Fetch the very first page of data from Supabase
        refresh()
    }


    private fun observePremiumStatus() {
        getPremiumStatusUseCase()
            .onEach { isPremium ->

                _uiState.update { state ->

                    val showAds = !isPremium

                    state.copy(
                        showAds = showAds,
                        adPositions =
                            if (showAds)
                                ensureAdPositions(state.prompts.size)
                            else
                                emptySet()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeNativeAds() {
        analytics.logEvent("native ad")
        adManager.nativeAdState
            .onEach { state ->

                _uiState.update {
                    it.copy(
                        nativeAdState = state
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setupOfflineFirstObservation() {
        uiState
            .map { it.selectedTab }
            .flatMapLatest { tab ->

                when (tab) {

                    HomeTab.POPULAR ->
                        getHomePromptsUseCase(HomeFeedType.POPULAR)

                    HomeTab.LATEST ->
                        getHomePromptsUseCase(HomeFeedType.LATEST)

                    HomeTab.REMIX ->
                        getRemixPromptsUseCase()
                }
            }
            .onEach { prompts ->

                val promptUi = prompts.map { it.toUi() }

                _uiState.update { state ->

                    val positions =
                        if (state.showAds) {
                            ensureAdPositions(promptUi.size)
                        } else {
                            emptySet()
                        }

                    state.copy(
                        prompts = promptUi,
                        adPositions = positions
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun setupHeroesAndUser() {
        getHomeHeroesUseCase()
            .onEach { heroes ->
                _uiState.update { it.copy(heroes = heroes.map { it.toUi() }) }
            }
            .launchIn(viewModelScope)

        getCurrentUserUseCase()
            .onEach { user ->
                _uiState.update { it.copy(currentUser = user?.toUi()) }
            }
            .launchIn(viewModelScope)
    }

    // --- Actions ---

    // 1. Pull-to-Refresh: Wipes cache and starts over at Page 0
    fun refresh() {

        adPositions.clear()

        if (isSyncing) return

        viewModelScope.launch {
            isSyncing = true
            currentPage = 0
            hasReachedEnd = false
            _uiState.update { it.copy(isLoading = true) }

            try {
                when (uiState.value.selectedTab) {

                    HomeTab.POPULAR ->
                        refreshHomeDataUseCase(HomeFeedType.POPULAR)

                    HomeTab.LATEST ->
                        refreshHomeDataUseCase(HomeFeedType.LATEST)

                    HomeTab.REMIX -> {
                        refreshHomeDataUseCase(HomeFeedType.POPULAR)
                        refreshHomeDataUseCase(HomeFeedType.LATEST)
                    }
                }
            } catch (e: Exception) {
                crashlytics.log("Home refresh failed")
                crashlytics.recordException(e)
                _uiState.update { it.copy(error = e.message ?: "Failed to refresh feed") }
            } finally {
                isSyncing = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 2. Infinite Scroll: Triggered when user reaches the bottom of the feed
    fun loadMore() {
        analytics.logEvent("home_load_more")
        if (isSyncing || hasReachedEnd || uiState.value.isLoading) return

        currentPage++ // Tell Room to expose 20 more items

        viewModelScope.launch {
            isSyncing = true
            _uiState.update { it.copy(isPaginating = true) }

            val result = when (uiState.value.selectedTab) {

                HomeTab.POPULAR ->
                    syncHomePromptsUseCase(
                        page = currentPage,
                        pageSize = 20,
                        feedType = HomeFeedType.POPULAR
                    )

                HomeTab.LATEST ->
                    syncHomePromptsUseCase(
                        page = currentPage,
                        pageSize = 20,
                        feedType = HomeFeedType.LATEST
                    )

                HomeTab.REMIX -> {

                    val popularResult = syncHomePromptsUseCase(
                        page = currentPage,
                        pageSize = 20,
                        feedType = HomeFeedType.POPULAR
                    )

                    syncHomePromptsUseCase(
                        page = currentPage,
                        pageSize = 20,
                        feedType = HomeFeedType.LATEST
                    )

                    popularResult
                }
            }

            result.onSuccess { isEnd ->
                hasReachedEnd = isEnd
            }.onFailure { error ->
                crashlytics.log("Pagination failed")
                crashlytics.recordException(error)

                _uiState.update { it.copy(error = error.message ?: "Failed to load more") }
            }

            isSyncing = false
            _uiState.update { it.copy(isPaginating = false) }
        }
    }

    fun onFeedSelected(feedType: HomeTab) {

        adPositions.clear()

        if (uiState.value.selectedTab == feedType) return

        viewModelScope.launch {
            currentPage = 0
            hasReachedEnd = false

            _uiState.update {
                it.copy(
                    selectedTab = feedType,
                    isPromptRefreshing = true
                )
            }

            try {
                when (uiState.value.selectedTab) {

                    HomeTab.POPULAR ->
                        refreshHomeDataUseCase(HomeFeedType.POPULAR)

                    HomeTab.LATEST ->
                        refreshHomeDataUseCase(HomeFeedType.LATEST)

                    HomeTab.REMIX -> {
                        refreshHomeDataUseCase(HomeFeedType.POPULAR)
                        refreshHomeDataUseCase(HomeFeedType.LATEST)
                    }
                }
            } catch (e: Exception) {
                crashlytics.log("Filter refresh failed")
                crashlytics.recordException(e)

                _uiState.update {
                    it.copy(error = e.message)
                }
            } finally {
                _uiState.update {
                    it.copy(isPromptRefreshing = false)
                }
            }
        }
    }
    // 3. Optimistic Updates
    fun onLikeClick(promptId: String) {

        println("LIKE: $promptId")
        analytics.logEvent(
            "prompt_liked",
            mapOf("prompt_id" to promptId)
        )
        viewModelScope.launch {
            // 🚀 MAGIC HAPPENS HERE: We ONLY fire the Use Case.
            // The Repository updates Room -> Room emits to flatMapLatest -> UI updates instantly.
            // We deleted all the messy manual UI state mapping!
            try {
                toggleLikeUseCase(promptId)
            } catch (e: Exception) {

                crashlytics.log("Like failed")
                crashlytics.setCustomKey("prompt_id", promptId)
                crashlytics.recordException(e)
            }
        }
    }

    fun onBookmarkClick(promptId: String) {
        viewModelScope.launch {
            // Same logic for bookmarks!
            toggleBookmarkUseCase(promptId)
        }
    }

    fun onPlayClick(promptId: String) {
        _uiState.update {
            it.copy(
                activeVideoId = if (it.activeVideoId == promptId) null else promptId
            )
        }
    }



    fun onPromptOpened(promptId: String) {
        analytics.logEvent(
            "prompt_opened",
            mapOf("prompt" to promptId)
        )
    }

    fun onSearchOpened() {
        analytics.logEvent("search_opened")
    }

    fun onProfileOpened() {
        analytics.logEvent("profile_opened")
    }

    fun onSharePrompt(prompt: PromptUi) {
        analytics.logEvent(
            "prompt_shared",
            mapOf("prompt" to prompt.title)
        )
    }

    fun onHeroClicked(title: String, ){
        analytics.logEvent("hero_clicked",
            mapOf("hero_id" to title)
        )
    }
  //inappmesasging event
    fun onHomeOpened() {
        inAppMessagingService.triggerEvent("home_opened")
    }


    private fun ensureAdPositions(
        totalPrompts: Int,
        firstAdAfter: Int = 8,
        minGap: Int = 6,
        maxGap: Int = 10
    ): Set<Int> {

        if (adPositions.isEmpty()) {
            adPositions += firstAdAfter
        }

        while (adPositions.last() < totalPrompts) {

            val next =
                adPositions.last() + (minGap..maxGap).random()

            adPositions += next
        }

        return adPositions.filter {
            it < totalPrompts
        }.toSet()
    }

}