package ai.achaialabs.helios.heliosApp.data.remote.datasource

import ai.achaialabs.helios.heliosApp.data.remote.dto.HomeHeroDto
import ai.achaialabs.helios.heliosApp.data.remote.dto.PromptDto
import ai.achaialabs.helios.heliosApp.data.remote.response.PromptApiResponse

interface PromptRemoteDataSource {
    // Default values allow you to call this with or without arguments
    suspend fun getHomePrompts(page: Int = 0, pageSize: Int = 20): List<PromptDto>

    suspend fun searchPrompts(query: String): List<PromptDto>
    suspend fun getHomeHeroes(): List<HomeHeroDto>
    suspend fun toggleLike(promptId: String): Boolean
    suspend fun toggleBookmark(promptId: String): Boolean
}