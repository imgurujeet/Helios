package ai.achaialabs.helios.heliosApp.ui.explore


import ai.achaialabs.helios.heliosApp.domain.usecase.GetPremiumStatusUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.explore.ObserveExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.explore.SyncExploreFeedUseCase
import ai.achaialabs.helios.heliosApp.firebase.Inappmessaging.InAppMessagingService
import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. Define the UI State
/**
 * Represents the UI state for the Explore screen.
 *
 * The list of categories always comes from Room.
 * Network only synchronizes data into Room.
 */
data class ExploreUiState(
    val isInitialLoading: Boolean = true,
    val isPaginating: Boolean = false,
    val categories: List<CategoryRowUi> = emptyList(),
    val error: String? = null
)

class ExploreViewModel(
    private val observeExploreFeedUseCase: ObserveExploreFeedUseCase,
    private val syncExploreFeedUseCase: SyncExploreFeedUseCase,
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val analytics: AnalyticsService,
    private val inAppMessagingService: InAppMessagingService
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    // Current pagination offset
    private var currentOffset = 0

    // Prevent duplicate network requests
    private var isSyncing = false

    // Stops requesting pages once server says there is nothing left
    private var hasReachedEnd = false

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Premium subscription state.
     */
    val isPremium: StateFlow<Boolean> =
        getPremiumStatusUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observeLocalDatabase()

        // Fetch first page.
        loadMore()
    }

    /**
     * Observe Room.
     *
     * Room is the single source of truth.
     * Whenever categories/prompts are inserted,
     * this Flow emits automatically and updates the UI.
     */
    private fun observeLocalDatabase() {

        observeExploreFeedUseCase().distinctUntilChanged()

            .map { categories ->

                categories .filter { it.prompts.isNotEmpty() }
                    .map { category ->
                    CategoryRowUi(
                        category = category.category.toUi(),
                        prompts = category.prompts.map { it.toUi() }
                    )
                }
            }
            .onEach { categories ->

                _uiState.update {

                    it.copy(
                        categories = categories,
                        isInitialLoading = false,
                        error = null
                    )
                }
            }
            .catch { throwable ->

                _uiState.update {

                    it.copy(
                        isInitialLoading = false,
                        error = throwable.message
                            ?: "Failed to load explore feed."
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Fetch the next page from the server.
     *
     * Newly fetched data is inserted into Room.
     * Room automatically updates the UI.
     */
    fun loadMore() {

        if (isSyncing || hasReachedEnd) return

        isSyncing = true

        viewModelScope.launch {

            try {

                _uiState.update {
                    it.copy(
                        isPaginating = currentOffset > 0,
                        error = null
                    )
                }

                val nextOffset = currentOffset

                syncExploreFeedUseCase(
                    limit = PAGE_SIZE,
                    offset = nextOffset
                )
                    .onSuccess { reachedEnd ->
                        hasReachedEnd = reachedEnd
                        currentOffset = nextOffset + PAGE_SIZE
                    }
                    .onFailure { throwable ->
                        _uiState.update {
                            it.copy(
                                error = throwable.message
                                    ?: "Unable to fetch explore feed."
                            )
                        }
                    }

            } finally {

                isSyncing = false

                _uiState.update {
                    it.copy(isPaginating = false)
                }
            }
        }
    }

    fun onCategoryOpened(
        categoryId: String,
        categoryName: String
    ) {
        analytics.logEvent(
            name = "category_opened",
            params = mapOf(
                "category_id" to categoryId,
                "category_name" to categoryName
            )
        )
    }

    fun onPromptOpened(
        promptId: String,
        categoryId: String,
        categoryName: String
    ) {
        analytics.logEvent(
            name = "prompt_opened",
            params = mapOf(
                "prompt_id" to promptId,
                "category_id" to categoryId,
                "category_name" to categoryName
            )
        )
    }

    fun viewAllOpened() {
        inAppMessagingService.triggerEvent("view_all_opened")
    }
}