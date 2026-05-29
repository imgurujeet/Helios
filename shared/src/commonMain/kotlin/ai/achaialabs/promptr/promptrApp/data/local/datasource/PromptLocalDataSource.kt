package ai.achaialabs.promptr.promptrApp.data.local.datasource

import ai.achaialabs.promptr.promptrApp.data.local.dao.PromptDao
import ai.achaialabs.promptr.promptrApp.data.local.entity.PromptEntity
import ai.achaialabs.promptr.promptrApp.data.mapper.toDomain
import ai.achaialabs.promptr.promptrApp.domain.model.Prompt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PromptLocalDataSource {
    fun getAllPrompts(): Flow<List<Prompt>>
    suspend fun getPromptById(id: String): PromptEntity?
    suspend fun insertPrompts(entities: List<PromptEntity>)
    suspend fun deleteAllPrompts()
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, newLikesCount: Int)
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)
}

class PromptLocalDataSourceImpl(
    private val promptDao: PromptDao
) : PromptLocalDataSource {
    override fun getAllPrompts(): Flow<List<Prompt>> {
        return promptDao.getAllPrompts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPromptById(id: String): PromptEntity? {
        return promptDao.getPromptById(id)
    }

    override suspend fun insertPrompts(entities: List<PromptEntity>) {
        promptDao.insertPrompts(entities)
    }

    override suspend fun deleteAllPrompts() {
        promptDao.deleteAllPrompts()
    }

    override suspend fun updateLikeStatus(id: String, isLiked: Boolean, newLikesCount: Int) {
        promptDao.updateLikeStatus(id, isLiked, newLikesCount)
    }

    override suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean) {
        promptDao.updateBookmarkStatus(id, isBookmarked)
    }
}
