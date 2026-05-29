package ai.achaialabs.promptr.promptrApp.domain.usecase

import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero
import ai.achaialabs.promptr.promptrApp.domain.model.isActive
import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class GetHomeHeroesUseCase(
    private val repository: PromptRepository
) {

    operator fun invoke(): Flow<List<HomeHero>> {

        return repository
            .getHomeHeroes()
            .map { heroes ->
                val now = Clock.System.now().toEpochMilliseconds()
                heroes.filter { it.isActive(now) }
            }
    }
}