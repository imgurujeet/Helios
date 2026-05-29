package ai.achaialabs.promptr.promptrApp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_heroes")
data class HomeHeroEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String,
    // Flattened HeroAction
    val actionType: String, // "OpenPrompt", "OpenCategory", "OpenUrl"
    val actionValue: String, // The ID or URL
    // Flattened HeroSchedule
    val startTimeMillis: Long?,
    val endTimeMillis: Long?
)
