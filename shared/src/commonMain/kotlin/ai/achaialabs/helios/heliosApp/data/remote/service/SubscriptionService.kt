package ai.achaialabs.helios.heliosApp.data.remote.service

import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.PurchasesAreCompletedBy
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SubscriptionManager(
    private val authRepository: AuthRepository
) {
    suspend fun syncSubscriptionStatus() {
        try {
            val customerInfo = getCustomerInfoSuspend()
            // Access the 'pro' entitlement safely
            val isPro = customerInfo.entitlements.active["pro"] != null

            authRepository.updateProStatus(isPro)
        } catch (e: Exception) {
            println("RevenueCat sync error: ${e.message}")
        }
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