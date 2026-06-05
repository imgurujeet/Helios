package ai.achaialabs.helios.heliosApp.data.local.dao

import ai.achaialabs.helios.heliosApp.data.local.entity.CategoryEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExploreDao {

    // Observes Categories up to a certain limit (Pagination)
    @Query("SELECT * FROM category_table ORDER BY name ASC LIMIT :limit")
    fun observeCategories(limit: Int): Flow<List<CategoryEntity>>

    // Fetches exactly 5 prompts for a specific category (Sync, not flow)
    @Query("SELECT * FROM prompts WHERE categoryId = :categoryId LIMIT 5")
    suspend fun getPromptsForCategory(categoryId: String): List<PromptEntity>

    @Query("""
        SELECT * FROM prompts 
        WHERE categoryId = :categoryId 
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    fun getPromptsByCategoryPaged(categoryId: String, limit: Int, offset: Int): Flow<List<PromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(prompts: List<PromptEntity>)
}