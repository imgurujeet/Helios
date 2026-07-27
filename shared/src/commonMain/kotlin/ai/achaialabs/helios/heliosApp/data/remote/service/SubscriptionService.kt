package ai.achaialabs.helios.heliosApp.data.remote.service

import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesAreCompletedBy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Clock

class SubscriptionManager(
    private val authRepository: AuthRepository
) {
    private var lastVerification = 0L

    companion object {
        private const val CACHE_TIME = 30 * 60 * 1000L // 30 min
    }

    suspend fun syncSubscriptionStatus(
        force: Boolean = false
    ) {

        val now = Clock.System.now().toEpochMilliseconds()

        if (!force && now - lastVerification < CACHE_TIME) {
            println("Using cached premium status.")
            return
        }

        lastVerification = now

        val customerInfo = getCustomerInfoSuspend()

        val isPro =
            customerInfo.entitlements["Helios Pro"]?.isActive == true

        authRepository.updateProStatus(isPro)


    }




    // Bridge for RevenueCat's callback-based API
    private suspend fun getCustomerInfoSuspend(): CustomerInfo = suspendCancellableCoroutine { continuation ->
        Purchases.sharedInstance.getCustomerInfo(
            onSuccess = { continuation.resume(it) },
            onError = { error ->
                continuation.resumeWithException(Exception(error.message))
            }
        )
    }
}