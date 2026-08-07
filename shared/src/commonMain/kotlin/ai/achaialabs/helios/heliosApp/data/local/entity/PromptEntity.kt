package ai.achaialabs.helios.heliosApp.data.local.entity

import ai.achaialabs.helios.heliosApp.domain.model.HomeFeedType
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey val id: String,
    val title: String,
    val promptText: String,
    val description: String?,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String?,
    val mediaType: String, // "image" or "video"
    val mediaUrl: String,
    val mediaThumbnailUrl: String?,
    val mediaDurationMs: Long?,
    val mediaAspectRatio: Float?,
    val categoryId: String,
    val categoryName: String,
    val categoryIconUrl: String?,
    val categoryImageUrl: String?,
    val likesCount: Int,
    val commentsCount: Int,
    val sharesCount: Int,
    val isLiked: Boolean,
    val isBookmarked: Boolean,
    val tags: String, // Comma separated
    val createdAt: Long,
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = false,
    val tooId: String,
    val tooName: String,
    val tooIconUrl: String?,
)


@Entity(
    tableName = "home_feed",
    primaryKeys = ["feedType", "position"]
)
data class HomeFeedEntity(

    val feedType: HomeFeedType,

    val position: Int,

    val promptId: String
)



@Entity(
    tableName = "category_prompt_cross_ref",
    primaryKeys = [
        "categoryId",
        "position"
    ]
)
data class CategoryPromptCrossRef(

    val categoryId: String,

    val promptId: String,

    val position: Int
)


data class HomePromptRelation(

    @Embedded
    val feed: HomeFeedEntity,

    @Relation(
        parentColumn = "promptId",
        entityColumn = "id"
    )
    val prompt: PromptEntity
)


data class CategoryPromptRelation(

    @Embedded
    val crossRef: CategoryPromptCrossRef,

    @Relation(
        parentColumn = "promptId",
        entityColumn = "id"
    )
    val prompt: PromptEntity
)