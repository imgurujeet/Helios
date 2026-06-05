package ai.achaialabs.helios.heliosApp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val token: String?,
    @ColumnInfo(name = "is_pro")
    val isPro: Boolean = false
)
