package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.random.Random

class GetRemixPromptsUseCase(
    private val repository: PromptRepository
) {

    private val remixSeed = Random.nextInt()

    private var order: List<String> = emptyList()

    operator fun invoke(): Flow<List<Prompt>> {
        return combine(
            repository.observeHomePrompts(HomeFeedType.POPULAR),
            repository.observeHomePrompts(HomeFeedType.LATEST)
        ) { popular, latest ->

            val merged = (popular + latest)
                .distinctBy { it.id }

            val ids = merged.map { it.id }

            if (ids != order) {
                order = ids.shuffled(Random(remixSeed))
            }

            val byId = merged.associateBy { it.id }

            order.mapNotNull { byId[it] }
        }
    }
}