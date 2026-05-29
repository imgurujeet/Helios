package ai.achaialabs.promptr.promptrApp.data.local.database

import ai.achaialabs.promptr.promptrApp.data.local.dao.HomeHeroDao
import ai.achaialabs.promptr.promptrApp.data.local.dao.PromptDao
import ai.achaialabs.promptr.promptrApp.data.local.dao.UserDao
import ai.achaialabs.promptr.promptrApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.promptr.promptrApp.data.local.entity.PromptEntity
import ai.achaialabs.promptr.promptrApp.data.local.entity.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PromptEntity::class, HomeHeroEntity::class, UserEntity::class],
    version = 2
)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun homeHeroDao(): HomeHeroDao
    abstract fun userDao(): UserDao
}
