package ai.achaialabs.promptr.promptrApp.data.local.dao

import ai.achaialabs.promptr.promptrApp.data.local.entity.HomeHeroEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeHeroDao {
    @Query("SELECT * FROM home_heroes")
    fun getAllHeroes(): Flow<List<HomeHeroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroes(heroes: List<HomeHeroEntity>)

    @Query("DELETE FROM home_heroes")
    suspend fun deleteAllHeroes()
}
