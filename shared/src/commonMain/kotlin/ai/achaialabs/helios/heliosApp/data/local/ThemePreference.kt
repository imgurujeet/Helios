package ai.achaialabs.helios.heliosApp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences(
    private val dataStore: DataStore<Preferences>
) {
    // Define the key used to save the theme state
    private val themeKey = booleanPreferencesKey("is_dark_theme")

    // A flow that emits true (Dark), false (Light), or null (System Default)
    val themeFlow: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[themeKey]
    }

    // Suspend function to save the choice
    suspend fun saveTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDark
        }
    }
}