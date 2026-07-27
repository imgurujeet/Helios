package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.model.ExploreCategory
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

interface ExploreRepository {
    // 1. Observe local data reactively based on a dynamic limit
    fun observeExploreFeed(): Flow<List<ExploreCategory>>

    // 2. Fetch from remote and sync to local DB
    suspend fun syncExploreFeed(limit: Int, offset: Int): Result<Boolean>

    fun observePromptsByCategory(categoryId: String, limit: Int, offset: Int): Flow<List<Prompt>>
    suspend fun syncPromptsForCategory(categoryId: String, limit: Int, offset: Int): Result<Boolean>
}