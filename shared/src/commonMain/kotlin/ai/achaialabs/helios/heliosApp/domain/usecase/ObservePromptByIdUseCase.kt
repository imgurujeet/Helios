package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class ObservePromptByIdUseCase(
    private val repository: PromptRepository
) {

    operator fun invoke(
        id: String
    ) = repository.observePromptById(id)
}