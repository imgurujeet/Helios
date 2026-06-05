package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class SyncHomePromptsUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int): Result<Boolean> {
        return repository.syncHomePrompts(page, pageSize)
    }
}