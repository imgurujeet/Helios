package ai.achaialabs.helios.heliosApp.ui.search

import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import ai.achaialabs.helios.heliosApp.domain.usecase.SearchPromptsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchPromptsUseCase: SearchPromptsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults = _searchQuery
        // 1. Increase debounce for database-heavy operations (300-500ms)
        .debounce(400)
        // 2. Only trigger if the text actually changed
        .distinctUntilChanged()
        .flatMapLatest { query ->

            val q = query.trim()

            if (q.length < 2) {
                flowOf(PagingData.empty())
            } else {

                // Fetch latest results from Supabase
                viewModelScope.launch {
                    searchPromptsUseCase.sync(q)
                }

                // Observe Room
                searchPromptsUseCase(q)
            }
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}