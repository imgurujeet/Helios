package ai.achaialabs.helios.heliosApp.data.local.dao

import ai.achaialabs.helios.heliosApp.data.local.entity.CategoryEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow



data class CategoryWithPrompts(

    @Embedded
    val category: CategoryEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val prompts: List<PromptEntity>
)

@Dao
interface ExploreDao {

    /**
     * Observe the explore feed.
     * Room automatically re-emits whenever either
     * categories or prompts are inserted/updated.
     */
    @Transaction
    @Query("""
        SELECT *
        FROM category_table
        ORDER BY id
    """)
    fun observeCategories(): Flow<List<CategoryWithPrompts>>

    /**
     * Observe prompts for a category.
     * Keeping the same method name.
     */
    @Query("""
        SELECT *
        FROM prompts
        WHERE categoryId = :categoryId
        ORDER BY createdAt DESC
        LIMIT 5
    """)
    fun getPromptsForCategory(
        categoryId: String
    ): Flow<List<PromptEntity>>

    @Query("""
        SELECT *
        FROM prompts
        WHERE categoryId = :categoryId
        ORDER BY createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getPromptsByCategoryPaged(
        categoryId: String,
        limit: Int,
        offset: Int
    ): Flow<List<PromptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(
        categories: List<CategoryEntity>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(
        prompts: List<PromptEntity>
    )

    /**
     * Insert categories and prompts in one transaction.
     */
    @Transaction
    suspend fun insertExploreFeed(
        categories: List<CategoryEntity>,
        prompts: List<PromptEntity>
    ) {
        insertCategories(categories)
        insertPrompts(prompts)
    }
}