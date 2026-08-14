package ai.achaialabs.helios.heliosApp.firebase.remoteconfig

interface RemoteConfigService {

    suspend fun fetchAndActivate()

    fun isRewardedAdFreeEnabled(): Boolean

    fun getRewardedAdFreeMinutes(): Int
}