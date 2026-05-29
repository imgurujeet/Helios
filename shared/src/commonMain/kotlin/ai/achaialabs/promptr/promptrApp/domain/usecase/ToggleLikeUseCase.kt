package ai.achaialabs.promptr.promptrApp.domain.usecase

import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository

class ToggleLikeUseCase(
    private val repository: PromptRepository
) {

    suspend operator fun invoke(
        promptId: String
    ) {

        repository.toggleLike(promptId)
    }
}