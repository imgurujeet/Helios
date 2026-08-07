package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class SyncHomePromptsUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int, feedType: HomeFeedType): Result<Boolean> {
        return repository.syncHomePrompts(page, pageSize, feedType)
    }
}