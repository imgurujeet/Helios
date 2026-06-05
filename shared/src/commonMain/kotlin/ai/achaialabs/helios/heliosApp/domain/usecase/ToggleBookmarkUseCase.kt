package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class ToggleBookmarkUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke(promptId: String) {
        repository.toggleBookmark(promptId)
    }
}