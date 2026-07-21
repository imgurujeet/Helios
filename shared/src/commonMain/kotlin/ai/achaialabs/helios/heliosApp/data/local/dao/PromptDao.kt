package ai.achaialabs.helios.heliosApp.data.local.dao

import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts ORDER BY createdAt DESC LIMIT :limit")
    fun getPromptsWithLimit(limit: Int): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(prompts: List<PromptEntity>)

    @Query("DELETE FROM prompts")
    suspend fun deleteAllPrompts()

    @Query("UPDATE prompts SET isLiked = :isLiked, likesCount = :newLikesCount WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, newLikesCount: Int)

    @Query("UPDATE prompts SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    @Query("""
    SELECT * FROM prompts 
    WHERE title LIKE '%' || :query || '%' 
    OR tags LIKE '%' || :query || '%' 
    OR categoryName LIKE '%' || :query || '%'
    ORDER BY createdAt DESC
""")
    fun searchPromptsPaging(query: String): PagingSource<Int, PromptEntity>

    @Query("SELECT * FROM prompts WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedPromptsPaging(): PagingSource<Int, PromptEntity>
}
