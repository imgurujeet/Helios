package ai.achaialabs.promptr.promptrApp.ui.home

import ai.achaialabs.promptr.promptrApp.domain.model.HeroAction
import ai.achaialabs.promptr.promptrApp.domain.usecase.GetHomeHeroesUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.GetHomePromptsUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.RefreshHomeDataUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.ToggleLikeUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.SyncUserUseCase
import ai.achaialabs.promptr.promptrApp.ui.mapper.toUi
import ai.achaialabs.promptr.promptrApp.ui.model.FeedMediaUi
import ai.achaialabs.promptr.promptrApp.ui.model.HomeHeroUi
import ai.achaialabs.promptr.promptrApp.ui.model.PromptUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomePromptsUseCase: GetHomePromptsUseCase,
    private val getHomeHeroesUseCase: GetHomeHeroesUseCase,
    private val refreshHomeDataUseCase: RefreshHomeDataUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update { it.copy(isLoading = true) }

        getHomePromptsUseCase()
            .onEach { prompts ->
                _uiState.update { it.copy(prompts = prompts.map { it.toUi() }) }
            }
            .launchIn(viewModelScope)

        getHomeHeroesUseCase()
            .onEach { heroes ->
                _uiState.update { it.copy(heroes = heroes.map { it.toUi() }) }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                refreshHomeDataUseCase()
            } catch (e: Exception) {
                println("HomeViewModel: Refresh Error: ${e.message}")
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }

        getCurrentUserUseCase()
            .onEach { user ->

                _uiState.update {
                    it.copy(
                        currentUser = user?.toUi()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onPlayClick(
        promptId: String
    ) {

        _uiState.update {

            it.copy(
                activeVideoId =
                    if(it.activeVideoId == promptId) {
                        null
                    } else {
                        promptId
                    }
            )
        }
    }

    fun onLikeClick(
        promptId: String
    ) {

        viewModelScope.launch {

            toggleLikeUseCase(
                promptId
            )

            _uiState.update { state ->

                state.copy(
                    prompts = state.prompts.map { prompt ->

                        if(prompt.id == promptId) {

                            val nowLiked =
                                !prompt.isLiked

                            prompt.copy(
                                isLiked = nowLiked,
                                likesCount =
                                    if(nowLiked) {
                                        prompt.likesCount + 1
                                    } else {
                                        prompt.likesCount - 1
                                    }
                            )

                        } else {
                            prompt
                        }
                    }
                )
            }
        }
    }



}