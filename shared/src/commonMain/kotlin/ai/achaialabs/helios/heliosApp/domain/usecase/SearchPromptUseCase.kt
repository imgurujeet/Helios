package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class SearchPromptsUseCase(private val repository: PromptRepository) {
    operator fun invoke(query: String): Flow<PagingData<Prompt>> {
        return repository.searchPrompts(query)
    }
}