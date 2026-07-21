package ai.achaialabs.helios.heliosApp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

// The name of the file saved on the device
expect class DataStoreProvider {
    fun getDataStore(): DataStore<Preferences>
}