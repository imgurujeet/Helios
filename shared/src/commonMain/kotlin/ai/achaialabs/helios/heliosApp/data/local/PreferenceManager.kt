package ai.achaialabs.helios.heliosApp.data.local

import ai.achaialabs.helios.heliosApp.data.datastore.DataStoreProvider
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock


enum class NavigationStyle {
    MATERIAL,
    FLOATING
}
class AppPreference(
    dataStoreProvider: DataStoreProvider
) {
    // Grab the platform-specific DataStore from the provider
    private val dataStore = dataStoreProvider.getDataStore()

    private val themeKey = booleanPreferencesKey("is_dark_theme")
    private val navigationStyleKey =
        stringPreferencesKey("navigation_style")
    val themeFlow: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[themeKey]
    }

    private val lastNotificationPromptTimeKey =
        longPreferencesKey("last_notification_prompt_time")

    val lastNotificationPromptTimeFlow: Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[lastNotificationPromptTimeKey] ?: 0L
        }

    suspend fun saveTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[themeKey] = isDark
        }
    }

    val navigationStyleFlow: Flow<NavigationStyle> =
        dataStore.data.map { preferences ->
            NavigationStyle.valueOf(
                preferences[navigationStyleKey]
                    ?: NavigationStyle.MATERIAL.name
            )
        }


    suspend fun saveNavigationStyle(style: NavigationStyle) {
        dataStore.edit { preferences ->
            preferences[navigationStyleKey] = style.name
        }
    }




    suspend fun saveLastNotificationPromptTime() {
        dataStore.edit { preferences ->
            preferences[lastNotificationPromptTimeKey] =
                Clock.System.now().toEpochMilliseconds()
        }
    }

    suspend fun resetLastNotificationPromptTime() {
        dataStore.edit { preferences ->
            preferences[lastNotificationPromptTimeKey] = 0L
        }
    }

    fun shouldShowPrompt(
        lastPromptTime: Long,
        permissionGranted: Boolean
    ): Boolean {

        if (permissionGranted) return false

        if (lastPromptTime == 0L) return true

        val twentyFourHours =
            24 * 60 * 60 * 1000L

        return Clock.System.now().toEpochMilliseconds() - lastPromptTime >= twentyFourHours
    }
}