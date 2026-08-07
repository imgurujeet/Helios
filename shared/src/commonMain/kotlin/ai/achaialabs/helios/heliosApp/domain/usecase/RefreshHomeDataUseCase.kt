package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository

class RefreshHomeDataUseCase(
    private val repository: PromptRepository
) {
    suspend operator fun invoke(feedType: HomeFeedType) {
        repository.refreshHomeData(feedType)
    }
}
