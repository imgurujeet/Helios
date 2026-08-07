package ai.achaialabs.helios.heliosApp.data.local.datasource

import ai.achaialabs.helios.heliosApp.data.local.dao.PromptDao
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.domain.filter.PromptFilter
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PromptLocalDataSource {

    suspend fun getPromptById(id: String): PromptEntity?

    fun observePromptById(
        id: String
    ): Flow<Prompt?>

    suspend fun insertPrompts(
        entities: List<PromptEntity>
    )

    suspend fun updateLikeStatus(
        id: String,
        isLiked: Boolean,
        newLikesCount: Int
    )

    suspend fun updateBookmarkStatus(
        id: String,
        isBookmarked: Boolean
    )
}

class PromptLocalDataSourceImpl(
    private val promptDao: PromptDao
) : PromptLocalDataSource {

    override suspend fun getPromptById(
        id: String
    ): PromptEntity? =
        promptDao.getPromptById(id)

    override fun observePromptById(
        id: String
    ): Flow<Prompt?> =
        promptDao.observePromptById(id)
            .map { it?.toDomain() }

    override suspend fun insertPrompts(
        entities: List<PromptEntity>
    ) {
        promptDao.insertPrompts(entities)
    }

    override suspend fun updateLikeStatus(
        id: String,
        isLiked: Boolean,
        newLikesCount: Int
    ) {
        promptDao.updateLikeStatus(
            id,
            isLiked,
            newLikesCount
        )
    }

    override suspend fun updateBookmarkStatus(
        id: String,
        isBookmarked: Boolean
    ) {
        promptDao.updateBookmarkStatus(
            id,
            isBookmarked
        )
    }
}