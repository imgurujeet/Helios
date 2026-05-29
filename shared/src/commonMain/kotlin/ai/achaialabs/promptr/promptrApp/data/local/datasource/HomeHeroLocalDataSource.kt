package ai.achaialabs.promptr.promptrApp.data.local.datasource

import ai.achaialabs.promptr.promptrApp.data.local.dao.HomeHeroDao
import ai.achaialabs.promptr.promptrApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.promptr.promptrApp.data.mapper.toDomain
import ai.achaialabs.promptr.promptrApp.domain.model.HomeHero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface HomeHeroLocalDataSource {
    fun getAllHeroes(): Flow<List<HomeHero>>
    suspend fun insertHeroes(entities: List<HomeHeroEntity>)
    suspend fun deleteAllHeroes()
}

class HomeHeroLocalDataSourceImpl(
    private val homeHeroDao: HomeHeroDao
) : HomeHeroLocalDataSource {
    override fun getAllHeroes(): Flow<List<HomeHero>> {
        return homeHeroDao.getAllHeroes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertHeroes(entities: List<HomeHeroEntity>) {
        homeHeroDao.insertHeroes(entities)
    }

    override suspend fun deleteAllHeroes() {
        homeHeroDao.deleteAllHeroes()
    }
}
