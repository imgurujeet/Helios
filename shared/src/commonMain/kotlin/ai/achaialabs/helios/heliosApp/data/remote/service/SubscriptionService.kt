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

            val isPro =
                customerInfo.entitlements["Helios Pro"]?.isActive == true


            //println("RC APP USER ID: ${Purchases.sharedInstance.appUserID}")

//            println(
//                "ACTIVE ENTITLEMENTS: ${
//                    customerInfo.entitlements.active.keys
//                }"
//            )
//
//            println(
//                "IS PRO: ${
//                    customerInfo.entitlements["pro"]?.isActive
//                }"
//            )
            authRepository.updateProStatus(isPro)

        } catch (e: Exception) {
            e.printStackTrace()
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