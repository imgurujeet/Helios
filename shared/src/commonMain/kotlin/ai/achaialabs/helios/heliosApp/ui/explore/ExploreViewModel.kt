package ai.achaialabs.helios.heliosApp.ui.explore


import ai.achaialabs.helios.heliosApp.domain.usecase.explore.ObserveExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.explore.SyncExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. Define the UI State
data class ExploreUiState(
    val isInitialLoading: Boolean = true, // True only on first ever app launch
    val isPaginating: Boolean = false,    // True when fetching the next page
    val categories: List<CategoryRowUi> = emptyList(),
    val error: String? = null
)

class ExploreViewModel(
    private val observeExploreFeedUseCase: ObserveExploreFeedUseCase,
    private val syncExploreFeedUseCase: SyncExploreFeedUseCase
) : ViewModel() {

    // Pagination trackers
    private val _currentLimit = MutableStateFlow(10) // Start with 10 categories
    private var currentOffset = 0
    private var isSyncing = false
    private var hasReachedEnd = false // Stops API calls if no more data exists

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        setupOfflineFirstObservation()
        syncInitialPage()
    }

    private fun setupOfflineFirstObservation() {
        // Whenever _currentLimit increases, re-query Room automatically!
        _currentLimit
            .flatMapLatest { limit -> observeExploreFeedUseCase(limit) }
            .map { domainCategories ->
                // MAP THE WHOLE LIST FROM DOMAIN TO UI
                //  FILTER OUT EMPTY CATEGORIES HERE
                domainCategories.filter { domainCat ->
                    domainCat.prompts.isNotEmpty()
                }.map { domainCat ->
                    // MAP ONLY THE ONES THAT SURVIVED THE FILTER
                    CategoryRowUi(
                        category = domainCat.category.toUi(),
                        prompts = domainCat.prompts.map { prompt -> prompt.toUi() }
                    )
                }
            }
            .onEach { localData ->
                _uiState.update {
                    it.copy(
                        categories = localData,
                        isInitialLoading = localData.isEmpty() && isSyncing
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun syncInitialPage() {
        syncNextPage(limit = 10, offset = 0)
    }

    // Called by the UI when the user scrolls near the bottom
    fun loadMore() {
        if (isSyncing || hasReachedEnd) return

        // Increase limits for the next fetch
        _currentLimit.value += 10
        currentOffset += 10

        syncNextPage(limit = 10, offset = currentOffset)
    }

    private fun syncNextPage(limit: Int, offset: Int) {
        viewModelScope.launch {
            isSyncing = true
            _uiState.update { it.copy(isPaginating = offset > 0) } // Show spinner at bottom

            val result = syncExploreFeedUseCase(limit, offset)

            result.onSuccess { isEnd ->
                hasReachedEnd = isEnd
                // If you want to be extremely precise, you can check if the API returned
                // fewer items than requested to set hasReachedEnd = true.
            }.onFailure { error ->
                _uiState.update { it.copy(error = error.message?:"A cosmic disturbance occurred") }
            }

            isSyncing = false
            _uiState.update { it.copy(isPaginating = false) }
        }
    }
}