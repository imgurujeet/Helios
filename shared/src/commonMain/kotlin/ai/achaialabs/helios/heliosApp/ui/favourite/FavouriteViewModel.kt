package ai.achaialabs.helios.heliosApp.ui.favourite

import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.usecase.GetLikedPromptsUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.ToggleLikeUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class FavouriteUiState(
    val title: String = "Loved Prompts",
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeVideoId: String? = null
)
class FavouriteViewModel(
    private val getLikedPromptsUseCase: GetLikedPromptsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouriteUiState())
    val uiState = _uiState.asStateFlow()

    // Paging stream
    val favoritePrompts: Flow<PagingData<Prompt>> = getLikedPromptsUseCase()
        .cachedIn(viewModelScope)

    fun onLikeClick(promptId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(promptId)
        }
    }

    fun updateError(message: String?) {
        _uiState.update { it.copy(error = message) }
    }

    fun onPlayClick(promptId: String) {
        _uiState.update {
            it.copy(
                activeVideoId = if (it.activeVideoId == promptId) null else promptId
            )
        }
    }
}

