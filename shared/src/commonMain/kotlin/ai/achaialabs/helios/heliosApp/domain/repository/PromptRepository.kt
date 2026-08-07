package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PromptRepository {

    fun searchPrompts(query: String): Flow<PagingData<Prompt>>

    suspend fun syncSearchResults(query: String)
    fun getLikedPrompts(): Flow<PagingData<Prompt>>
    fun observeHomePrompts(feedType: HomeFeedType): Flow<List<Prompt>>
    fun getHomeHeroes(): Flow<List<HomeHero>>

    suspend fun syncHomePrompts(page: Int, pageSize: Int,feedType: HomeFeedType): Result<Boolean>
    suspend fun refreshHomeData(feedType: HomeFeedType): Result<Unit>

    fun observePromptById(
        promptId: String
    ): Flow<Prompt?>

    suspend fun toggleLike(promptId: String)
    suspend fun toggleBookmark(promptId: String)
}