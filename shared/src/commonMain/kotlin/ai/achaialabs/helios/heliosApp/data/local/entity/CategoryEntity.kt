package ai.achaialabs.helios.heliosApp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_table")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String?,
    val iconUrl: String?,
    @ColumnInfo(name = "is_premium")
    val isPremium: Boolean = false
)