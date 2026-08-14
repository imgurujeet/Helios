package ai.achaialabs.helios.heliosApp.domain.service

import ai.achaialabs.helios.heliosApp.data.local.AppPreference
import ai.achaialabs.helios.heliosApp.firebase.remoteconfig.RemoteConfigService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class AdFreeAccessManager(
    private val appPreference: AppPreference,
    private val remoteConfigService: RemoteConfigService
) {

    /**
     * Timestamp until which the user has temporary ad-free access.
     */
    val adFreeUntil: Flow<Long> =
        appPreference.adFreeUntilFlow

    /**
     * Whether temporary ad-free access is currently active.
     *
     * This is recalculated whenever the stored expiry timestamp changes.
     */
    val isAdFreeActive: Flow<Boolean> =
        appPreference.adFreeUntilFlow.map { until ->
            until > now()
        }

    /**
     * Grants temporary ad-free access after a rewarded ad
     * has been successfully completed.
     *
     * If the user already has active access, the new duration
     * is added to the existing expiry instead of resetting it.
     */
    suspend fun grantRewardedAdFreeAccess(): Boolean {

        // Feature remotely disabled
        if (!remoteConfigService.isRewardedAdFreeEnabled()) {
            return false
        }

        val minutes =
            remoteConfigService.getRewardedAdFreeMinutes()

        // Invalid configuration
        if (minutes <= 0) {
            return false
        }

        val currentTime = now()

        val currentAdFreeUntil =
            appPreference.adFreeUntilFlow
                .first()

        /*
         * If access is still active:
         *
         * currentAdFreeUntil = future
         *
         * Extend from that point.
         *
         * Otherwise:
         *
         * start from current time.
         */
        val baseTime =
            maxOf(currentTime, currentAdFreeUntil)

        val durationMillis =
            minutes * 60_000L

        val newAdFreeUntil =
            baseTime + durationMillis

        appPreference.saveAdFreeUntil(
            newAdFreeUntil
        )

        return true
    }

    /**
     * Returns how many milliseconds of temporary
     * ad-free access remain.
     *
     * Returns 0 if access has expired.
     */
    suspend fun getRemainingTimeMillis(): Long {

        val until =
            appPreference.adFreeUntilFlow.first()

        return maxOf(
            0L,
            until - now()
        )
    }

    /**
     * Returns remaining time in minutes.
     */
    suspend fun getRemainingTimeMinutes(): Long {

        return getRemainingTimeMillis() / 60_000L
    }

    /**
     * Clears temporary ad-free access.
     *
     * Mainly useful for logout/debugging.
     */
    suspend fun clearAccess() {
        appPreference.clearAdFreeUntil()
    }

    private fun now(): Long =
        Clock.System.now()
            .toEpochMilliseconds()
}