package ai.achaialabs.helios.heliosApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tool")
data class ToolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconUrl: String
)