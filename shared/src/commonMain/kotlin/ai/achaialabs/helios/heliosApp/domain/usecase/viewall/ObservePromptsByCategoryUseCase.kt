package ai.achaialabs.helios.heliosApp.domain.usecase.viewall

import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.Flow

class ObservePromptsByCategoryUseCase(private val repository: ExploreRepository) {
    operator fun invoke(categoryId: String, limit: Int, offset: Int): Flow<List<Prompt>> {
        return repository.observePromptsByCategory(categoryId, limit, offset)
    }
}