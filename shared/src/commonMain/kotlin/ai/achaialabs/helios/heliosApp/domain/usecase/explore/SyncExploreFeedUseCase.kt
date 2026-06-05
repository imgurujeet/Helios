package ai.achaialabs.helios.heliosApp.domain.usecase.explore

import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository

class SyncExploreFeedUseCase(
    private val repository: ExploreRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): Result<Boolean> {
        return repository.syncExploreFeed(limit, offset)
    }
}