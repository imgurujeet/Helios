package ai.achaialabs.promptr.promptrApp.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(): RoomDatabase.Builder<PromptDatabase> {
    val dbFilePath = documentDirectory() + "/prompt.db"
    return Room.databaseBuilder<PromptDatabase>(
        name = dbFilePath,
        factory =  { PromptDatabase::class.instantiateImpl() }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
  val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
    directory = NSDocumentDirectory,
    inDomain = NSUserDomainMask,
    appropriateForURL = null,
    create = false,
    error = null,
  )
  return documentDirectory?.path ?: ""
}