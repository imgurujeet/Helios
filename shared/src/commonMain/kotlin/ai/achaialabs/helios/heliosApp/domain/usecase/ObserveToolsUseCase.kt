package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.model.Tool
import ai.achaialabs.helios.heliosApp.domain.repository.PromptDetailRepository
import kotlinx.coroutines.flow.Flow

class ObserveToolsUseCase(
    private val repository: PromptDetailRepository
) {

    operator fun invoke(): Flow<List<Tool>> {
        return repository.observeTools()
    }
}