package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.dao.PromptDao
import ai.achaialabs.helios.heliosApp.data.local.datasource.HomeHeroLocalDataSource
import ai.achaialabs.helios.heliosApp.data.local.datasource.PromptLocalDataSource
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.datasource.PromptRemoteDataSource
import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PromptRepositoryImpl(
    private val localPromptDataSource: PromptLocalDataSource,
    private val localHeroDataSource: HomeHeroLocalDataSource,
    private val remoteDataSource: PromptRemoteDataSource,
    private val promptDao: PromptDao,
) : PromptRepository {

    override fun searchPrompts(query: String): Flow<PagingData<Prompt>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                promptDao.searchPromptsPaging(query)
            }
        ).flow.map { pagingData ->
            // Use your existing domain mapper
            pagingData.map { it.toDomain() }
        }
    }

    // 1. OBSERVE: UI listens to this. As limit grows, Room emits more items.
    override fun observeHomePrompts(limit: Int): Flow<List<Prompt>> {
        return localPromptDataSource.getPromptsWithLimit(limit)
    }

    override fun getHomeHeroes(): Flow<List<HomeHero>> {
        return localHeroDataSource.getAllHeroes()
    }

    // 2. PAGINATE: Fetch the next page and ADD it to Room (Do not delete!)
    override suspend fun syncHomePrompts(page: Int, pageSize: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val remotePrompts = remoteDataSource.getHomePrompts(page = page, pageSize = pageSize)

                // Insert the new page into Room. Because we are observing Room,
                // the UI will update automatically.
                localPromptDataSource.insertPrompts(remotePrompts.map { it.toEntity() })

                // Return TRUE if we reached the end of the feed
                remotePrompts.size < pageSize
            }
        }
    }

    // 3. PULL-TO-REFRESH: Wipes everything and starts over
    override suspend fun refreshHomeData(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {


                // Fetch page 0 for prompts, and fetch heroes
                val remotePrompts = remoteDataSource.getHomePrompts(page = 0, pageSize = 20)
                val remoteHeroes = remoteDataSource.getHomeHeroes()

                // Clear old cache and insert fresh data
                localPromptDataSource.deleteAllPrompts()
                localPromptDataSource.insertPrompts(remotePrompts.map { it.toEntity() })

                localHeroDataSource.deleteAllHeroes()
                localHeroDataSource.insertHeroes(remoteHeroes.map { it.toEntity() })
            }
        }
    }

    // --- Optimistic Updates (Unchanged) ---
    override suspend fun toggleLike(promptId: String) {
        // 1. Fetch the absolute latest from local DB
        val currentEntity = localPromptDataSource.getPromptById(promptId) ?: return

        // 2. Prepare the new state
        val isNowLiked = !currentEntity.isLiked
        val newLikesCount = if (isNowLiked) currentEntity.likesCount + 1 else (currentEntity.likesCount - 1).coerceAtLeast(0)

        // 3. APPLY LOCALLY FIRST (Optimistic UI)
        localPromptDataSource.updateLikeStatus(promptId, isNowLiked, newLikesCount)

        // 4. SYNC WITH SERVER
        val isSuccess = try {
            remoteDataSource.toggleLike(promptId)
        } catch (e: Exception) {
            false // Treat any crash as a failure
        }

        // 5. RECONCILE (Only if failed)
        if (!isSuccess) {
            // Rollback only if the server explicitly failed
            localPromptDataSource.updateLikeStatus(promptId, currentEntity.isLiked, currentEntity.likesCount)

        }
    }

    override suspend fun toggleBookmark(promptId: String) {
        val currentEntity = localPromptDataSource.getPromptById(promptId) ?: return
        val newBookmarkStatus = !currentEntity.isBookmarked

        localPromptDataSource.updateBookmarkStatus(promptId, newBookmarkStatus)
        val isSuccess = remoteDataSource.toggleBookmark(promptId)
        if (!isSuccess) {
            localPromptDataSource.updateBookmarkStatus(promptId, currentEntity.isBookmarked)
        }
    }// In PromptRepositoryImpl.kt

    override fun getLikedPrompts(): Flow<PagingData<Prompt>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                promptDao.getLikedPromptsPaging()
            }
        ).flow // 1. Convert Pager to Flow
            .map { pagingData ->
                // 2. Map Entity to Domain
                pagingData.map { it.toDomain() }
            }
    }
}