package ai.achaialabs.promptr.promptrApp.data.repository

import ai.achaialabs.promptr.promptrApp.data.local.datasource.HomeHeroLocalDataSource
import ai.achaialabs.promptr.promptrApp.data.local.datasource.PromptLocalDataSource
import ai.achaialabs.promptr.promptrApp.data.mapper.toEntity
import ai.achaialabs.promptr.promptrApp.data.remote.datasource.PromptRemoteDataSource
import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero
import ai.achaialabs.promptr.promptrApp.domain.model.Prompt
import ai.achaialabs.promptr.promptrApp.domain.repository.PromptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class PromptRepositoryImpl(
    private val localPromptDataSource: PromptLocalDataSource,
    private val localHeroDataSource: HomeHeroLocalDataSource,
    private val remoteDataSource: PromptRemoteDataSource
) : PromptRepository {

    override fun getHomePrompts(): Flow<List<Prompt>> {
        return localPromptDataSource.getAllPrompts()
            .onStart { 
                // Trigger refresh whenever someone starts listening
                refreshHomeData() 
            }
    }

    override fun getHomeHeroes(): Flow<List<HomeHero>> {
        return localHeroDataSource.getAllHeroes()
    }

    override suspend fun refreshHomeData() {
        try {
            println("PromptRepository: Fetching dummy data from remote...")
            val remotePrompts = remoteDataSource.getHomePrompts()
            val remoteHeroes = remoteDataSource.getHomeHeroes()

            println("PromptRepository: Saving ${remotePrompts.size} prompts to local DB...")
            localPromptDataSource.deleteAllPrompts()
            localPromptDataSource.insertPrompts(remotePrompts.map { it.toEntity() })

            localHeroDataSource.deleteAllHeroes()
            localHeroDataSource.insertHeroes(remoteHeroes.map { it.toEntity() })
            println("PromptRepository: Refresh complete.")
        } catch (e: Exception) {
            println("PromptRepository: Refresh failed - ${e.message}")
        }
    }

    override suspend fun toggleLike(promptId: String) {
        val currentEntity = localPromptDataSource.getPromptById(promptId) ?: return
        val newLikeStatus = !currentEntity.isLiked
        val newLikesCount = if (newLikeStatus) currentEntity.likesCount + 1 else (currentEntity.likesCount - 1).coerceAtLeast(0)
        localPromptDataSource.updateLikeStatus(promptId, newLikeStatus, newLikesCount)
    }

    override suspend fun toggleBookmark(promptId: String) {
        val currentEntity = localPromptDataSource.getPromptById(promptId) ?: return
        localPromptDataSource.updateBookmarkStatus(promptId, !currentEntity.isBookmarked)
    }
}
