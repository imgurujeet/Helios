package ai.achaialabs.helios.heliosApp.data.local.dao

import ai.achaialabs.helios.heliosApp.data.local.entity.HomeFeedEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.HomePromptRelation
import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeFeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeds(
        items: List<HomeFeedEntity>
    )

    @Query("DELETE FROM home_feed WHERE feedType = :feedType")
    suspend fun clearFeed(
        feedType: HomeFeedType
    )

    @Query("""
    DELETE FROM home_feed
    WHERE promptId = :promptId
""")
    suspend fun deletePromptFromHomeFeed(
        promptId: String
    )

    @Transaction
    @Query("""
        SELECT *
        FROM home_feed
        WHERE feedType = :feedType
        ORDER BY position
    """)
    fun observeHomeFeed(
        feedType: HomeFeedType,
    ): Flow<List<HomePromptRelation>>
}




class HomeFeedTypeConverter {

    @TypeConverter
    fun fromType(type: HomeFeedType): String = type.name

    @TypeConverter
    fun toType(value: String): HomeFeedType =
        HomeFeedType.valueOf(value)
}