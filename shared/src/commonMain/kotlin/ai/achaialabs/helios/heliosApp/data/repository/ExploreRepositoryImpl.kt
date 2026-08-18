package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.dao.ExploreDao
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.remote.datasource.ExploreRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toDto
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toPromptDto
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.ExploreCategory
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remoteDataSource: ExploreRemoteDataSource,
    private val exploreDao: ExploreDao,
    private val crashlytics: CrashlyticsService
) : ExploreRepository {

    // This watches the local database and streams updates to the UI

    override  fun observeExploreFeed(): Flow<List<ExploreCategory>> {
        return exploreDao.observeCategories().map { categoriesWithPrompts ->
            categoriesWithPrompts.map { item ->
                ExploreCategory(
                    category = item.category.toDomain(),
                    prompts = item.prompts
                        .take(6) // Keep only the first 6 for the Explore screen
                        .map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun deleteCategoryLocally(
        categoryId: String
    ) {
        exploreDao.deletePromptsForCategory(categoryId)
        exploreDao.deleteCategory(categoryId)
    }

    override suspend fun deletePromptLocally(
        promptId: String
    ) {
        exploreDao.deletePrompt(promptId)
    }


    override suspend fun observeRealtimeChanges() {

        coroutineScope {

            // ============================================================
            // CATEGORY REALTIME
            // ============================================================

            launch {

                remoteDataSource
                    .observeCategoryChanges()
                    .collect { change ->

                        println(
                            "EXPLORE REALTIME CATEGORY EVENT: $change"
                        )

                        when (change) {

                            is PostgresAction.Delete -> {

                                val categoryId =
                                    change.oldRecord["id"]
                                        ?.toString()
                                        ?.trim('"')

                                println(
                                    "EXPLORE REALTIME CATEGORY DELETE: $categoryId"
                                )

                                if (!categoryId.isNullOrBlank()) {

                                    deleteCategoryLocally(
                                        categoryId
                                    )

                                    println(
                                        "EXPLORE ROOM CATEGORY DELETED: $categoryId"
                                    )
                                }
                            }

                            is PostgresAction.Insert -> {

                                println(
                                    "EXPLORE REALTIME CATEGORY INSERT"
                                )

                                syncExploreFeed(
                                    limit = 10,
                                    offset = 0
                                )
                            }

                            is PostgresAction.Update -> {

                                println(
                                    "EXPLORE REALTIME CATEGORY UPDATE"
                                )

                                syncExploreFeed(
                                    limit = 10,
                                    offset = 0
                                )
                            }

                            else -> Unit
                        }
                    }
            }


            // ============================================================
            // PROMPT REALTIME
            // ============================================================

            launch {

                remoteDataSource
                    .observePromptChanges()
                    .collect { change ->

                        println(
                            "EXPLORE REALTIME PROMPT EVENT: $change"
                        )

                        when (change) {

                            is PostgresAction.Delete -> {

                                val categoryId =
                                    change.oldRecord["id"]
                                        ?.toString()
                                        ?.trim('"')

                                if (!categoryId.isNullOrBlank()) {
                                    deleteCategoryLocally(categoryId)
                                }
                            }

                            is PostgresAction.Insert -> {

                                println(
                                    "EXPLORE REALTIME PROMPT INSERT"
                                )

                                syncExploreFeed(
                                    limit = 10,
                                    offset = 0
                                )
                            }

                            is PostgresAction.Update -> {

                                println(
                                    "EXPLORE REALTIME PROMPT UPDATE"
                                )

                                syncExploreFeed(
                                    limit = 10,
                                    offset = 0
                                )
                            }

                            else -> Unit
                        }
                    }
            }
        }
    }

    // 2. THE SYNC FUNCTION (Satisfies the second half of the interface)
    // This fetches from Supabase and dumps it into the local database
    override suspend fun syncExploreFeed(
        limit: Int,
        offset: Int
    ): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {

                // 1. Fetch categories
                val categoriesResult =
                    remoteDataSource
                        .fetchCategories(limit, offset)
                        .getOrThrow()

                val categoryEntities =
                    categoriesResult.map {
                        it.toDto().toEntity()
                    }

                // 2. Fetch prompts for those categories
                val categoryIds =
                    categoryEntities.map { it.id }

                val promptsResult =
                    remoteDataSource
                        .fetchPromptsForCategories(categoryIds)
                        .getOrThrow()

                val promptEntities =
                    promptsResult.map {
                        it.toPromptDto().toEntity()
                    }

                // 3. Insert/update Room
                exploreDao.insertExploreFeed(
                    categories = categoryEntities,
                    prompts = promptEntities
                )

                // 4. Pagination
                categoriesResult.size < limit

            }.onFailure { e ->

                crashlytics.log("syncExploreFeed failed")
                crashlytics.setCustomKey(
                    "limit",
                    limit.toString()
                )
                crashlytics.setCustomKey(
                    "offset",
                    offset.toString()
                )
                crashlytics.recordException(e)
            }
        }
    }
    override fun observePromptsByCategory(categoryId: String, limit: Int, offset: Int): Flow<List<Prompt>> {
        return exploreDao.getPromptsByCategoryPaged(categoryId, limit, offset)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncPromptsForCategory(categoryId: String, limit: Int, offset: Int): Result<Boolean> {
        return runCatching {
            // Fetch from Supabase
            val remotePrompts = remoteDataSource.fetchPromptsByCategory(categoryId, limit, offset).getOrThrow()
            // Save to Room
            exploreDao.insertPrompts(remotePrompts.map { it.toPromptDto().toEntity() })
            remotePrompts.size < limit
        }.onFailure { e ->

            crashlytics.log("syncPromptsForCategory failed")

            crashlytics.setCustomKey("category_id", categoryId)
            crashlytics.setCustomKey("limit", limit.toString())
            crashlytics.setCustomKey("offset", offset.toString())

            crashlytics.recordException(e)
        }
    }


}