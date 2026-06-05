package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.PromptDetailRepository

class SyncToolsUseCase(
    private val repository: PromptDetailRepository
) {

    suspend operator fun invoke(): Result<Unit> {
        return repository.syncTools()
    }
}