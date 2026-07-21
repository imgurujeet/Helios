package ai.achaialabs.helios.heliosApp.ad

import ai.achaialabs.helios.BuildKonfig
import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AndroidAdManager(
    private val context: Context
) : AdManager {

    private var rewardedAd: RewardedAd? = null

    private var nativeAd: NativeAd? = null
    private val _rewardedAdState =
        MutableStateFlow<RewardedAdState>(
            RewardedAdState.Idle
        )

    override val rewardedAdState =
        _rewardedAdState.asStateFlow()

    private val _nativeAdState =
        MutableStateFlow<NativeAdState>(
            NativeAdState.Idle
        )

    override val nativeAdState =
        _nativeAdState.asStateFlow()


    override fun preloadRewardedAd() {

        if (rewardedAd != null) return

        _rewardedAdState.value =
            RewardedAdState.Loading

        RewardedAd.load(
            context,
            //"ca-app-pub-3940256099942544/5224354917",
            BuildKonfig.REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),

            object : RewardedAdLoadCallback() {

                override fun onAdLoaded(
                    ad: RewardedAd
                ) {

                    rewardedAd = ad

                    _rewardedAdState.value =
                        RewardedAdState.Loaded
                }

                override fun onAdFailedToLoad(
                    error: LoadAdError
                ) {

                    rewardedAd = null

                    _rewardedAdState.value =
                        RewardedAdState.Error(
                            error.message
                        )
                }
            }
        )
    }

    override fun showRewardedAd(
        onRewardEarned: () -> Unit
    ) {

        val activity =
            ActivityProvider.currentActivity ?: return

        val ad = rewardedAd ?: return

        _rewardedAdState.value =
            RewardedAdState.Showing

        ad.show(activity) {

            onRewardEarned()

            rewardedAd = null

            _rewardedAdState.value =
                RewardedAdState.Idle

            preloadRewardedAd()
        }
    }



    @RequiresPermission(Manifest.permission.INTERNET)
    override fun preloadNativeAd() {

        if (nativeAd != null) return

        _nativeAdState.value =
            NativeAdState.Loading

        val adLoader = AdLoader.Builder(
            context,
            //"ca-app-pub-3940256099942544/2247696110",
            BuildKonfig.NATIVE_AD_UNIT_ID
        )
            .forNativeAd { ad ->

                nativeAd?.destroy()

                nativeAd = ad

                _nativeAdState.value =
                    NativeAdState.Loaded
            }
            .withAdListener(
                object : AdListener() {

                    override fun onAdFailedToLoad(
                        error: LoadAdError
                    ) {

                        _nativeAdState.value =
                            NativeAdState.Error(
                                error.message
                            )
                    }
                }
            )
            .build()

        adLoader.loadAd(
            AdRequest.Builder().build()
        )
    }


    override fun clear() {

        rewardedAd = null

        nativeAd?.destroy()
        nativeAd = null
    }

    override fun getNativeAd(): NativeAd? {
        return nativeAd
    }
}