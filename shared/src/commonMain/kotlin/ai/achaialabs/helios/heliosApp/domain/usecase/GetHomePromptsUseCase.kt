package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow

class GetHomePromptsUseCase(
    private val repository: PromptRepository
) {

    // Pass the limit parameter to observe the correct chunk of data
    operator fun invoke(feedType: HomeFeedType): Flow<List<Prompt>> {
        return repository.observeHomePrompts(feedType = feedType)
    }
}