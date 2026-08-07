package ai.achaialabs.helios.heliosApp.data.local.dao

import ai.achaialabs.helios.heliosApp.data.local.entity.HomeFeedEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.HomePromptRelation
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptEntity?

    @Query("""
    SELECT *
    FROM prompts
    WHERE id = :id
    LIMIT 1
""")
    fun observePromptById(
        id: String
    ): Flow<PromptEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(
        prompts: List<PromptEntity>
    )

    @Query("""
        UPDATE prompts
        SET
            isLiked = :isLiked,
            likesCount = :newLikesCount
        WHERE id = :id
    """)
    suspend fun updateLikeStatus(
        id: String,
        isLiked: Boolean,
        newLikesCount: Int
    )

    @Query("""
        UPDATE prompts
        SET isBookmarked = :isBookmarked
        WHERE id = :id
    """)
    suspend fun updateBookmarkStatus(
        id: String,
        isBookmarked: Boolean
    )

    @Query("DELETE FROM prompts")
    suspend fun deleteAll()

    @Query("""
        SELECT *
        FROM prompts
        WHERE
            title LIKE '%' || :query || '%'
            OR tags LIKE '%' || :query || '%'
            OR categoryName LIKE '%' || :query || '%'
    """)
    fun searchPromptsPaging(
        query: String
    ): PagingSource<Int, PromptEntity>

    @Query("""
        SELECT *
        FROM prompts
        WHERE isLiked = 1
    """)
    fun getLikedPromptsPaging(): PagingSource<Int, PromptEntity>
}



