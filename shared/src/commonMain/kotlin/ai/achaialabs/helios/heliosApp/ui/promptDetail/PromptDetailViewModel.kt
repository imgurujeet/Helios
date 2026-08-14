package ai.achaialabs.helios.heliosApp.ui.promptDetail

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.ad.RewardedAdState
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.model.Tool
import ai.achaialabs.helios.heliosApp.domain.service.AdFreeAccessManager
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ObservePromptByIdUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ObserveToolsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.SyncToolsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleBookmarkUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.ObservePromptsByCategoryUseCase
import ai.achaialabs.helios.heliosApp.firebase.Inappmessaging.InAppMessagingService
import ai.achaialabs.helios.heliosApp.firebase.remoteconfig.RemoteConfigService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class PromptDetailUiState(

    // Whole screen state
    val isLoading: Boolean = false,
    val error: String? = null,

    // Prompt Detail Data
    val prompts: List<Prompt> = emptyList(),
    val initialPageIndex: Int = 0,

    // Tool Section State
    val isToolsLoading: Boolean = false,
    val tools: List<Tool> = emptyList(),
    val toolsError: String? = null,
    val activeVideoId: String? = null,
    val isProUser: Boolean = false,
    val revealedPrompts: Set<String> = emptySet(),
    val rewardedAdState: RewardedAdState =
        RewardedAdState.Idle,
    val isAdFreeActive: Boolean = false,
    val adFreeUntil: Long = 0L,
    val adFreeMinutes: Int = 0,
    val showAds: Boolean = true,
    val nativeAdState: NativeAdState=
        NativeAdState.Idle

)



class PromptDetailViewModel(
    private val observeToolsUseCase: ObserveToolsUseCase,
    private val syncToolsUseCase: SyncToolsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observePromptsByCategoryUseCase: ObservePromptsByCategoryUseCase,
    private val adManager: AdManager,
    private val inAppMessagingService: InAppMessagingService,
    private val adFreeAccessManager: AdFreeAccessManager,
    private val observePromptByIdUseCase: ObservePromptByIdUseCase,
    private val remoteConfigService: RemoteConfigService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptDetailUiState())

    // Public immutable state exposed to UI
    val uiState: StateFlow<PromptDetailUiState> = _uiState.asStateFlow()

    private var feedJob: Job? = null
    private var categoryJob: Job? = null
    private var observedCategoryId: String? = null

    private var initialPageSet = false


    init {
        // STEP 1: Start observing Room database changes for tools
        loadAdFreeDuration()
        observeTools()
        observeNativeAds()

        // STEP 2: Fetch latest tools from API and save them into Room database
        syncTools()

        // STEP 3: Observe user state
        getCurrentUserUseCase()
            .onEach { user ->
                _uiState.update { it.copy(isProUser = user?.isPro ?: false,
                    showAds = !(user?.isPro ?: false)) }
            }
            .launchIn(viewModelScope)


        adManager.rewardedAdState
            .onEach { adState ->

                _uiState.update {
                    it.copy(
                        rewardedAdState = adState
                    )
                }
            }
            .launchIn(viewModelScope)
        uiState.map { it.isProUser }
            .distinctUntilChanged()
            .onEach { isPro ->
                if (!isPro) {
                    adManager.preloadRewardedAd()
                }
            }
            .launchIn(viewModelScope)


        adFreeAccessManager.adFreeUntil
            .onEach { until ->

                val isActive =
                    until > kotlin.time.Clock.System
                        .now()
                        .toEpochMilliseconds()

                _uiState.update {
                    it.copy(
                        adFreeUntil = until,
                        isAdFreeActive = isActive
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    private fun observeNativeAds() {
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

    private fun loadAdFreeDuration() {
        _uiState.update {
            it.copy(
                adFreeMinutes =
                    remoteConfigService.getRewardedAdFreeMinutes()
            )
        }
    }

    // Call this from your Compose screen via a LaunchedEffect
    fun initializeFeed(
        clickedPromptId: String
    ) {

        feedJob?.cancel()
        categoryJob?.cancel()

        observedCategoryId = null
        initialPageSet = false


        _uiState.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        feedJob =
            observePromptByIdUseCase(clickedPromptId)
                .onEach { prompt ->

                    if (prompt == null) {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Prompt not found"
                            )
                        }

                        return@onEach
                    }

                    if (observedCategoryId != prompt.category.id) {
                        observedCategoryId = prompt.category.id

                        observeCategory(
                            categoryId = prompt.category.id,
                            clickedPromptId = clickedPromptId
                        )
                    }

                }
                .launchIn(viewModelScope)
    }

    private fun observeCategory(
        categoryId: String,
        clickedPromptId: String
    ) {

        categoryJob?.cancel()

        categoryJob =
            observePromptsByCategoryUseCase(
                categoryId = categoryId,
                limit = 100,
                offset = 0
            )
                .onEach { prompts ->

                    val index =
                        prompts.indexOfFirst {
                            it.id == clickedPromptId
                        }

                    if (index == -1) {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Prompt not found"
                            )
                        }

                        return@onEach
                    }

                    if (!initialPageSet) {

                        initialPageSet = true

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                prompts = prompts,
                                initialPageIndex = index
                            )
                        }

                    } else {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                prompts = prompts
                            )
                        }

                    }

                }
                .launchIn(viewModelScope)
    }

    private fun observeTools() {
        observeToolsUseCase()
            // This Flow emits whenever Room DB updates
            .onEach { tools ->
                // Update UI state with latest local DB data
                _uiState.update {
                    it.copy(tools = tools)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun syncTools() {
        viewModelScope.launch {
            // Show loading state before API call
            _uiState.update {
                it.copy(
                    isToolsLoading = true,
                    toolsError = null
                )
            }

            // Fetch from API -> save into Room DB
            syncToolsUseCase()
                .onSuccess {
                    // Stop loading state (Room Flow handles the actual data update)
                    _uiState.update {
                        it.copy(isToolsLoading = false)
                    }
                }
                .onFailure { throwable ->
                    // Show error if API/database fails
                    _uiState.update {
                        it.copy(
                            isToolsLoading = false,
                            toolsError = throwable.message
                        )
                    }
                }
        }
    }

    // --- Actions ---

    fun onLikeClick(promptId: String) {
        viewModelScope.launch {
            // Fire and Forget! Room handles the rest.
            toggleLikeUseCase(promptId)
        }
    }

    fun onBookmarkClick(promptId: String) {
        viewModelScope.launch {
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


    fun revealPrompt(
        promptId: String
    ) {

        val currentState = _uiState.value

        // 1. Pro user → everything they are entitled to can open
        if (currentState.isProUser) {
            unlockPrompt(promptId)
            return
        }

        // 2. Temporary ad-free access → free prompts can open
        if (currentState.isAdFreeActive) {
            unlockPrompt(promptId)
            return
        }

        when (currentState.rewardedAdState) {

            RewardedAdState.Loaded -> {
                println("Loaded -> show ad")
                adManager.showRewardedAd {
                    viewModelScope.launch {
                        adFreeAccessManager.grantRewardedAdFreeAccess()
                        unlockPrompt(promptId)
                    }
                }
            }

            RewardedAdState.Loading -> {
                println("Loading")
                // loader already shown in UI
            }

            RewardedAdState.Showing -> {
                println("Showing")

                // prevent multiple taps
            }

            RewardedAdState.Idle -> {
                println("Idle")
            }
            is RewardedAdState.Error -> {
                println("Error -> fallback UX")
                // fallback UX

                unlockPrompt(promptId)

                adManager.preloadRewardedAd()
            }
        }
    }

    private fun unlockPrompt(
        promptId: String
    ) {

        _uiState.update { currentState ->

            currentState.copy(
                revealedPrompts =
                    currentState.revealedPrompts + promptId
            )
        }
    }

    fun onPromptDetailOpened() {
        inAppMessagingService.triggerEvent("prompt_detail_opened")
    }
}