package ai.achaialabs.helios.heliosApp.ad

import kotlinx.coroutines.flow.StateFlow

interface AdManager {

    val rewardedAdState: StateFlow<RewardedAdState>

    val nativeAdState: StateFlow<NativeAdState>
    fun preloadRewardedAd()

    fun showRewardedAd(
        onRewardEarned: () -> Unit
    )

    fun preloadNativeAd()

    fun getNativeAd(): Any?

    fun clear()
}