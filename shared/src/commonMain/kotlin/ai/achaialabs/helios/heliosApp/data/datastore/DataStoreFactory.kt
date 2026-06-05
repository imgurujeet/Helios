package ai.achaialabs.helios.heliosApp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

// The name of the file saved on the device
internal const val DATA_STORE_FILE_NAME = "helios_prefs.preferences_pb"

// Shared builder function
fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
}