package ai.achaialabs.helios.heliosApp.ui.viewall

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.ad.NativeAdState
import ai.achaialabs.helios.heliosApp.domain.usecase.GetPremiumStatusUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.ObservePromptsByCategoryUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.viewall.SyncPromptsByCategoryUseCase
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewAllViewModel(
    private val categoryId: String,
    private val categoryName: String,
    private val observePrompts: ObservePromptsByCategoryUseCase,
    private val syncPrompts: SyncPromptsByCategoryUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val adManager: AdManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViewAllUiState())
    val uiState = _uiState.asStateFlow()

    // --- Exactly like your HomeViewModel ---
    private val _currentLimit = MutableStateFlow(20)
    private var currentOffset = 0
    private var isSyncing = false
    private var hasReachedEnd = false

    init {
        _uiState.update { it.copy(categoryName = categoryName) }
        observePremiumStatus()
        setupOfflineFirstObservation()
        observeNativeAds()
        // Fetch the first page
        loadMore()
    }

    private fun observePremiumStatus() {
        getPremiumStatusUseCase()
            .onEach { isPremium ->
                _uiState.update {
                    it.copy(
                        showAds = !isPremium
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

    private fun setupOfflineFirstObservation() {
        // Now it reacts to the limit changing!
        _currentLimit
            .flatMapLatest { limit -> observePrompts(categoryId, limit, offset = 0) }
            .onEach { prompts ->
                _uiState.update { state ->
                    state.copy(
                        prompts = prompts.map { p -> p.toUi() },
                        isLoading = prompts.isEmpty() && isSyncing
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun loadMore() {
        if (isSyncing || hasReachedEnd) return
        isSyncing = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            syncPrompts(categoryId, limit = 20, offset = currentOffset)
                .onSuccess {
                    currentOffset += 20
                    _currentLimit.value += 20 // Tell Room to show more items!
                }
                .onFailure {
                    _uiState.update { it.copy(error = "Could not load more") }
                }

            isSyncing = false
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onLikeClick(promptId: String) {
        viewModelScope.launch {
            // 🚀 MAGIC HAPPENS HERE: We ONLY fire the Use Case.
            // The Repository updates Room -> Room emits to flatMapLatest -> UI updates instantly.
            // We deleted all the messy manual UI state mapping!
            toggleLikeUseCase(promptId)
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

data class ViewAllUiState(
    val categoryName: String = "",
    val prompts: List<PromptUi> = emptyList(),
    val isLoading: Boolean = false,
    val activeVideoId: String? = null,
    val error: String? = null,
    val showAds: Boolean = false,
    val nativeAdState: NativeAdState =
        NativeAdState.Idle,
)