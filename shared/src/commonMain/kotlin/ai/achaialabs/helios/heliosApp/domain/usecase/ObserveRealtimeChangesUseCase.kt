package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository


class ObserveRealtimeChangesUseCase(
    private val repository: ExploreRepository
) {
    suspend operator fun invoke() {
        repository.observeRealtimeChanges()
    }
}