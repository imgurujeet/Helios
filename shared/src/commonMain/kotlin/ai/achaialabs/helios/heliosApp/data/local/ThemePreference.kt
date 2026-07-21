package ai.achaialabs.helios.heliosApp.data.local

import ai.achaialabs.helios.heliosApp.data.datastore.DataStoreProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences(
    dataStoreProvider: DataStoreProvider // <-- Inject the provider
) {
    // Grab the platform-specific DataStore from the provider
    private val dataStore = dataStoreProvider.getDataStore()

    private val themeKey = booleanPreferencesKey("is_dark_theme")

    val themeFlow: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[themeKey]
    }

    suspend fun saveTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDark
        }
    }
}