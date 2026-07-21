package ai.achaialabs.helios.heliosApp.data.local.database

import ai.achaialabs.helios.heliosApp.data.local.dao.ExploreDao
import ai.achaialabs.helios.heliosApp.data.local.dao.HomeHeroDao
import ai.achaialabs.helios.heliosApp.data.local.dao.PromptDao
import ai.achaialabs.helios.heliosApp.data.local.dao.ToolDao
import ai.achaialabs.helios.heliosApp.data.local.dao.UserDao
import ai.achaialabs.helios.heliosApp.data.local.entity.CategoryEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.HomeHeroEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.PromptEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.ToolEntity
import ai.achaialabs.helios.heliosApp.data.local.entity.UserEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PromptEntity::class, HomeHeroEntity::class, UserEntity::class, CategoryEntity::class, ToolEntity::class],
    version = 10
)
abstract class PromptDatabase : RoomDatabase() {
    abstract fun promptDao(): PromptDao
    abstract fun homeHeroDao(): HomeHeroDao
    abstract fun userDao(): UserDao

    abstract fun exploreDao(): ExploreDao

    abstract fun toolDao(): ToolDao

}
