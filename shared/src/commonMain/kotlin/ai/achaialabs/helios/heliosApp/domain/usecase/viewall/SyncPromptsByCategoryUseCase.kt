package ai.achaialabs.helios.heliosApp.domain.usecase.viewall

import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository

class SyncPromptsByCategoryUseCase(private val repository: ExploreRepository) {
    suspend operator fun invoke(categoryId: String, limit: Int, offset: Int): Result<Boolean> {
        return repository.syncPromptsForCategory(categoryId, limit, offset)
    }
}