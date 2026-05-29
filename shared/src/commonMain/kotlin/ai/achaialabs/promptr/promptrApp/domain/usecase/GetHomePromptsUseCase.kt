package ai.achaialabs.promptr.promptrApp.domain.usecase

import ai.achaialabs.promptr.promptrApp.domain.model.Prompt
import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow

class GetHomePromptsUseCase(
    private val repository: PromptRepository
) {

    operator fun invoke(): Flow<List<Prompt>> {

        return repository.getHomePrompts()
    }
}