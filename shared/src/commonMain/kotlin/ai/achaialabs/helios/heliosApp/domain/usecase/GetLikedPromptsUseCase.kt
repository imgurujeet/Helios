package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

// domain/usecase/GetLikedPromptsUseCase.kt
class GetLikedPromptsUseCase(
    private val promptRepository: PromptRepository
) {
    // Return Flow<PagingData<Prompt>> instead of Flow<List<Prompt>>
    operator fun invoke(): Flow<PagingData<Prompt>> {
        return promptRepository.getLikedPrompts()
    }
}