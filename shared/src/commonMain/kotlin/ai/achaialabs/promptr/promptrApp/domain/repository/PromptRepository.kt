package ai.achaialabs.promptr.promptrApp.domain.repository

import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero
import ai.achaialabs.promptr.promptrApp.domain.model.Prompt
import kotlinx.coroutines.flow.Flow

interface PromptRepository {

    fun getHomePrompts(): Flow<List<Prompt>>

    fun getHomeHeroes(): Flow<List<HomeHero>>

    suspend fun refreshHomeData()

    suspend fun toggleLike(
        promptId: String
    )

    suspend fun toggleBookmark(
        promptId: String
    )
}
