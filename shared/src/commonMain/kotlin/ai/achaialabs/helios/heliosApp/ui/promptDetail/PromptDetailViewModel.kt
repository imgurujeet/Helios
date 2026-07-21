package ai.achaialabs.helios.heliosApp.ui.promptDetail

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.ad.RewardedAdState
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.model.Tool
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ObserveToolsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.SyncToolsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleBookmarkUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.ObservePromptsByCategoryUseCase
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
        RewardedAdState.Idle

)



class PromptDetailViewModel(
    private val observeToolsUseCase: ObserveToolsUseCase,
    private val getHomePromptsUseCase: GetHomePromptsUseCase,
    private val categoryId: String?,
    private val syncToolsUseCase: SyncToolsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observePromptsByCategoryUseCase: ObservePromptsByCategoryUseCase,
    private val adManager: AdManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptDetailUiState())

    // Public immutable state exposed to UI
    val uiState: StateFlow<PromptDetailUiState> = _uiState.asStateFlow()

    // Flag to ensure we only find the initial index once
    private var initializedForPromptId: String? = null
    private var itemsToDrop = 0
    private var feedJob: Job? = null

    init {
        // STEP 1: Start observing Room database changes for tools
        observeTools()

        // STEP 2: Fetch latest tools from API and save them into Room database
        syncTools()

        // STEP 3: Observe user state
        getCurrentUserUseCase()
            .onEach { user ->
                _uiState.update { it.copy(isProUser = user?.isPro ?: false) }
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
    }

    // Call this from your Compose screen via a LaunchedEffect
    fun initializeFeed(clickedPromptId: String) {

        feedJob?.cancel()

        val feedFlow = if (categoryId != null) {
            observePromptsByCategoryUseCase(
                categoryId,
                limit = 100,
                offset = 0
            )
        } else {
            getHomePromptsUseCase(limit = 100)
        }

        feedJob = feedFlow
            .onEach { fullFeed ->

                val clickedIndex =
                    fullFeed.indexOfFirst {
                        it.id == clickedPromptId
                    }.coerceAtLeast(0)

                val slicedFeed =
                    fullFeed.drop(clickedIndex)

                _uiState.update {
                    it.copy(
                        isLoading = slicedFeed.isEmpty(),
                        prompts = slicedFeed,
                        initialPageIndex = 0
                    )
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

        if (currentState.isProUser) {

            unlockPrompt(promptId)

            return
        }

        when (currentState.rewardedAdState) {

            RewardedAdState.Loaded -> {

                adManager.showRewardedAd {

                    unlockPrompt(promptId)
                }
            }

            RewardedAdState.Loading -> {

                // loader already shown in UI
            }

            RewardedAdState.Showing -> {

                // prevent multiple taps
            }

            RewardedAdState.Idle,
            is RewardedAdState.Error -> {

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
}