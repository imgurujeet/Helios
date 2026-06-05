package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.isActive
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
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