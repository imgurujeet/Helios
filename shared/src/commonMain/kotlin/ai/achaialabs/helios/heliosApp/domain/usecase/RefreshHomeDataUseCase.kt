package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class RefreshHomeDataUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke() {
        repository.refreshHomeData()
    }
}
