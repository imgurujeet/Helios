package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.dao.PromptDao
import ai.achaialabs.helios.heliosApp.data.local.datasource.HomeFeedLocalDataSource
import ai.achaialabs.helios.heliosApp.data.local.datasource.HomeHeroLocalDataSource
import ai.achaialabs.helios.heliosApp.data.local.datasource.PromptLocalDataSource
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.datasource.PromptRemoteDataSource
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import ai.achaialabs.helios.heliosApp.domain.model.HomeHero
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.PromptRepository
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
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
    private val localHomeFeedDataSource: HomeFeedLocalDataSource,
    private val localHeroDataSource: HomeHeroLocalDataSource,
    private val remoteDataSource: PromptRemoteDataSource,
    private val promptDao: PromptDao,
    private val crashlytics: CrashlyticsService
) : PromptRepository {



    override fun observePromptById(
        promptId: String
    ): Flow<Prompt?> =
        localPromptDataSource.observePromptById(promptId)

    override fun searchPrompts(
        query: String,
    ): Flow<PagingData<Prompt>> {
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
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun syncSearchResults(
        query: String,
    ) {

        if (query.length < 2) return


        val remote = remoteDataSource.searchPrompts(query)

        localPromptDataSource.insertPrompts(
            remote.map {
                it.toEntity()
            }
        )
    }


    // 1. OBSERVE: UI listens to this. As limit grows, Room emits more items.
    override fun observeHomePrompts(
        feedType: HomeFeedType
    ): Flow<List<Prompt>> {

        return localHomeFeedDataSource
            .observeFeed(feedType)
            .map { relations ->
                relations.map {
                    it.prompt.toDomain()
                }
            }
    }

    override fun getHomeHeroes(): Flow<List<HomeHero>> {
        return localHeroDataSource.getAllHeroes()
    }

    override suspend fun syncHomePrompts(
        page: Int,
        pageSize: Int,
        feedType: HomeFeedType
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {

                val remotePrompts = remoteDataSource.getHomePrompts(
                    page = page,
                    pageSize = pageSize,
                    feedType = feedType
                )

                val promptEntities = remotePrompts.map { it.toEntity() }

                // Insert/update prompt data
                localPromptDataSource.insertPrompts(promptEntities)

                // Cache feed ordering
                localHomeFeedDataSource.appendFeed(
                    feedType = feedType,
                    promptIds = promptEntities.map { it.id },
                    startPosition = page * pageSize
                )

                // true = no more pages
                remotePrompts.size < pageSize

            }.onFailure { e ->
                crashlytics.log("Sync Home Prompt failed")
                crashlytics.recordException(e)
            }
        }
    }

    override suspend fun refreshHomeData(
        feedType: HomeFeedType
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {

                val remotePrompts = remoteDataSource.getHomePrompts(
                    page = 0,
                    pageSize = 20,
                    feedType = feedType
                )

                val remoteHeroes = remoteDataSource.getHomeHeroes()

                val promptEntities = remotePrompts.map { it.toEntity() }

                // Refresh prompts
                localPromptDataSource.insertPrompts(promptEntities)

                // Replace this feed's ordering only
                localHomeFeedDataSource.replaceFeed(
                    feedType = feedType,
                    promptIds = promptEntities.map { it.id }
                )

                // Refresh heroes
                localHeroDataSource.deleteAllHeroes()
                localHeroDataSource.insertHeroes(
                    remoteHeroes.map { it.toEntity() }
                )

            }.onFailure { e ->
                crashlytics.log("Refresh Home Data failed")
                crashlytics.recordException(e)
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
            crashlytics.log("toggleLike failed")
            crashlytics.recordException(e)
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