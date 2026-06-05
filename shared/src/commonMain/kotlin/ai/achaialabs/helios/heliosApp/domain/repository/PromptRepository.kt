package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

interface PromptRepository {
    fun observeHomePrompts(limit: Int): Flow<List<Prompt>>
    fun getHomeHeroes(): Flow<List<HomeHero>>

    suspend fun syncHomePrompts(page: Int, pageSize: Int): Result<Boolean>
    suspend fun refreshHomeData(): Result<Unit>

    suspend fun toggleLike(promptId: String)
    suspend fun toggleBookmark(promptId: String)
}