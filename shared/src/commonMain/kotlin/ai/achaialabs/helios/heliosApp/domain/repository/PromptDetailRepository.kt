package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.model.Tool
import kotlinx.coroutines.flow.Flow

interface PromptDetailRepository{

    fun observeTools(): Flow<List<Tool>>
    suspend fun syncTools(): Result<Unit>
}