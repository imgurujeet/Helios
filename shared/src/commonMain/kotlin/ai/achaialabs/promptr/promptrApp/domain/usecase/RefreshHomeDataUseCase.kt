package ai.achaialabs.promptr.promptrApp.domain.usecase

import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository

class RefreshHomeDataUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke() {
        repository.refreshHomeData()
    }
}
