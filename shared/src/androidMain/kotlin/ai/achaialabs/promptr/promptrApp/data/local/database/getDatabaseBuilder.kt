package ai.achaialabs.promptr.promptrApp.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
// Assuming there's a way to get context or we pass it

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PromptDatabase> {
    val dbFile = context.getDatabasePath("prompt.db")
    return Room.databaseBuilder<PromptDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}