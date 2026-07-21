package ai.achaialabs.helios.heliosApp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual class DataStoreProvider(private val context: Context) {
    actual fun getDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                // Uses Android's context to find the files directory
                File(context.filesDir, "helios_prefs.preferences_pb").absolutePath.toPath()
            }
        )
    }
}