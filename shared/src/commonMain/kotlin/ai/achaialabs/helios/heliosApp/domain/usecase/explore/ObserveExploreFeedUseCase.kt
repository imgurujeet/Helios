package ai.achaialabs.helios.heliosApp.domain.usecase.explore

import ai.achaialabs.helios.heliosApp.domain.model.ExploreCategory
import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository
import kotlinx.coroutines.flow.Flow

class ObserveExploreFeedUseCase(
    private val repository: ExploreRepository
) {
    operator fun invoke(limit: Int): Flow<List<ExploreCategory>> {
        return repository.observeExploreFeed(limit)
    }
}