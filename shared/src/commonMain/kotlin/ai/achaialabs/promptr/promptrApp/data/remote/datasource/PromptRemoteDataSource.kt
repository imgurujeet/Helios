package ai.achaialabs.promptr.promptrApp.data.remote.datasource

import ai.achaialabs.promptr.promptrApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.promptr.promptrApp.data.remote.dto.PromptDto

interface PromptRemoteDataSource {
    suspend fun getHomePrompts(): List<PromptDto>
    suspend fun getHomeHeroes(): List<HomeHeroDto>
    suspend fun toggleLike(promptId: String): Boolean
    suspend fun toggleBookmark(promptId: String): Boolean
}
