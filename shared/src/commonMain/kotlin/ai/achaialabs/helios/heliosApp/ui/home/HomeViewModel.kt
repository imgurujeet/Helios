package ai.achaialabs.helios.heliosApp.ui.home

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomeHeroesUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.GetHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.RefreshHomeDataUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleBookmarkUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.SyncHomePromptsUseCase
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val adManager: AdManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    // --- Pagination Trackers ---
    private val _currentLimit = MutableStateFlow(20) // Initial limit
    private var currentPage = 0
    private var isSyncing = false
    private var hasReachedEnd = false

    init {
        setupOfflineFirstObservation()
        setupHeroesAndUser()
        observeNativeAds()

        // Fetch the very first page of data from Supabase
        refresh()
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

    private fun setupOfflineFirstObservation() {
        // UI listens to Room. Room limits based on _currentLimit.
        _currentLimit
            .flatMapLatest { limit -> getHomePromptsUseCase(limit) }
            .onEach { prompts ->
                _uiState.update { state ->
                    state.copy(
                        prompts = prompts.map { it.toUi() },
                        // Stop initial loading once Room has data
                        isLoading = prompts.isEmpty() && isSyncing
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
        if (isSyncing) return

        viewModelScope.launch {
            isSyncing = true
            currentPage = 0
            hasReachedEnd = false
            _currentLimit.value = 20 // Reset Room observation limit
            _uiState.update { it.copy(isLoading = true) }

            try {
                refreshHomeDataUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to refresh feed") }
            } finally {
                isSyncing = false
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 2. Infinite Scroll: Triggered when user reaches the bottom of the feed
    fun loadMore() {
        if (isSyncing || hasReachedEnd || uiState.value.isLoading) return

        currentPage++
        _currentLimit.value += 20 // Tell Room to expose 20 more items

        viewModelScope.launch {
            isSyncing = true
            _uiState.update { it.copy(isPaginating = true) }

            val result = syncHomePromptsUseCase(page = currentPage, pageSize = 20)

            result.onSuccess { isEnd ->
                hasReachedEnd = isEnd
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message ?: "Failed to load more") }
            }

            isSyncing = false
            _uiState.update { it.copy(isPaginating = false) }
        }
    }

    // 3. Optimistic Updates
    fun onLikeClick(promptId: String) {
        viewModelScope.launch {
            // 🚀 MAGIC HAPPENS HERE: We ONLY fire the Use Case.
            // The Repository updates Room -> Room emits to flatMapLatest -> UI updates instantly.
            // We deleted all the messy manual UI state mapping!
            toggleLikeUseCase(promptId)
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
}