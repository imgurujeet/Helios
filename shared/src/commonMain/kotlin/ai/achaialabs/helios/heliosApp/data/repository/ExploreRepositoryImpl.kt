package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.dao.ExploreDao
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.remote.datasource.ExploreRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toDto
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toPromptDto
import ai.achaialabs.helios.heliosApp.domain.model.ExploreCategory
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.repository.ExploreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExploreRepositoryImpl(
    private val remoteDataSource: ExploreRemoteDataSource,
    private val exploreDao: ExploreDao
) : ExploreRepository {

    // This watches the local database and streams updates to the UI
    override fun observeExploreFeed(categoryLimit: Int): Flow<List<ExploreCategory>> {
        return exploreDao.observeCategories(limit = categoryLimit).map { categoryEntities ->
            categoryEntities.map { categoryEntity ->
                // Fetch the 5 prompts for each category directly from the local DB
                val promptEntities = exploreDao.getPromptsForCategory(categoryEntity.id)

                ExploreCategory(
                    category = categoryEntity.toDomain(),
                    prompts = promptEntities.map { it.toDomain() }
                )
            }
        }
    }

    // 2. THE SYNC FUNCTION (Satisfies the second half of the interface)
    // This fetches from Supabase and dumps it into the local database
    override suspend fun syncExploreFeed(limit: Int, offset: Int): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                // 1. FETCH API RESPONSES
                val categoriesResult = remoteDataSource.fetchCategories(limit, offset).getOrThrow()
                categoriesResult.size < limit
                // 2. MAP & SAVE CATEGORIES: ApiResponse -> DTO -> Entity -> Room
                val categoryEntities = categoriesResult.map { it.toDto().toEntity() }
                exploreDao.insertCategories(categoryEntities)

                // 3. FETCH PROMPTS (Using the category IDs we just got)
                val categoryIds = categoryEntities.map { it.id }
                val promptsResult = remoteDataSource.fetchPromptsForCategories(categoryIds).getOrThrow()

                // 4. MAP & SAVE PROMPTS: ApiResponse -> DTO -> Entity -> Room
                val promptEntities = promptsResult.map { it.toPromptDto().toEntity() }
                exploreDao.insertPrompts(promptEntities)

                categoriesResult.size < limit

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
        }
    }




}