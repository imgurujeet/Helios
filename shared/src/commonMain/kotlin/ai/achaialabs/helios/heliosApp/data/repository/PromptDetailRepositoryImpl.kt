package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.dao.ToolDao
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.remote.datasource.ToolRemoteDataSource
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toDto
import ai.achaialabs.helios.heliosApp.data.remote.mapper.toEntity
import ai.achaialabs.helios.heliosApp.domain.model.Tool
import ai.achaialabs.helios.heliosApp.domain.repository.PromptDetailRepository
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PromptDetailRepositoryImpl (
    private val toolRemoteDataSource: ToolRemoteDataSource,
    private val toolDao: ToolDao,
    private val crashlytics: CrashlyticsService
): PromptDetailRepository {

    override fun observeTools(): Flow<List<Tool>> {
        return toolDao.getAllTools().map { toolEntities ->
            toolEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    // Fetch from API and cache locally
    override suspend fun syncTools(): Result<Unit> {

        return withContext(Dispatchers.IO) {

            runCatching {

                // 1. FETCH FROM API
                val toolsResult =
                    toolRemoteDataSource
                        .fetchAllTools()
                        .getOrThrow()

                // 2. MAP API -> DTO -> ENTITY
                val toolEntities =
                    toolsResult.map { apiResponse ->
                        apiResponse
                            .toDto()
                            .toEntity()
                    }

                // 3. SAVE TO ROOM
                toolDao.insertTools(toolEntities)
            }.onFailure { e ->
                crashlytics.log("syncTools failed")
                crashlytics.recordException(e)
            }
        }
    }

}