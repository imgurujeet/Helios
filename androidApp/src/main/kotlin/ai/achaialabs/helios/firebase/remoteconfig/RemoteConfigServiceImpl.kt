package ai.achaialabs.helios.firebase.remoteconfig

import ai.achaialabs.helios.heliosApp.firebase.remoteconfig.RemoteConfigService
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

class RemoteConfigServiceImpl : RemoteConfigService {

    private val remoteConfig: FirebaseRemoteConfig =
        Firebase.remoteConfig

    init {
        remoteConfig.setDefaultsAsync(
            mapOf(
                KEY_REWARDED_AD_FREE_ENABLED to true,
                KEY_REWARDED_AD_FREE_MINUTES to 1L
            )
        )

        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
        )
    }

    override suspend fun fetchAndActivate() {
        remoteConfig.fetchAndActivate().await()
    }

    override fun isRewardedAdFreeEnabled(): Boolean =
        remoteConfig.getBoolean(
            KEY_REWARDED_AD_FREE_ENABLED
        )

    override fun getRewardedAdFreeMinutes(): Int =
        remoteConfig.getLong(
            KEY_REWARDED_AD_FREE_MINUTES
        ).toInt()

    private companion object {
        const val KEY_REWARDED_AD_FREE_ENABLED =
            "rewarded_ad_free_enabled"

        const val KEY_REWARDED_AD_FREE_MINUTES =
            "rewarded_ad_free_minutes"
    }
}