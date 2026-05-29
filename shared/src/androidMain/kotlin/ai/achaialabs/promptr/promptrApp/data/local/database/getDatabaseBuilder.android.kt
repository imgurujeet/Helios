package ai.achaialabs.promptr.promptrApp.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import ai.achaialabs.promptr.promptrApp.utils.Haptics

actual fun getDatabaseBuilder(): RoomDatabase.Builder<PromptDatabase> {
    val context = Haptics.appContext ?: throw IllegalStateException("Context not initialized. Call Haptics.init(context) first.")
    val dbFile = context.getDatabasePath("prompt.db")
    return Room.databaseBuilder<PromptDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}