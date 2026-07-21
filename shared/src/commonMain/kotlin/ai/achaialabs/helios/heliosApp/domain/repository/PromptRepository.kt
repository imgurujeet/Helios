package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PromptRepository {

    fun searchPrompts(query: String): Flow<PagingData<Prompt>>

    fun getLikedPrompts(): Flow<PagingData<Prompt>>
    fun observeHomePrompts(limit: Int): Flow<List<Prompt>>
    fun getHomeHeroes(): Flow<List<HomeHero>>

    suspend fun syncHomePrompts(page: Int, pageSize: Int): Result<Boolean>
    suspend fun refreshHomeData(): Result<Unit>

    suspend fun toggleLike(promptId: String)
    suspend fun toggleBookmark(promptId: String)
}