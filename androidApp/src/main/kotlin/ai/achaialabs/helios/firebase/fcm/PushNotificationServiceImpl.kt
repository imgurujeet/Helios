package ai.achaialabs.helios.firebase.fcm

import ai.achaialabs.helios.heliosApp.firebase.fcm.PushNotificationService
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PushNotificationServiceImpl(
    private val context: Context
) : PushNotificationService {

    companion object {
        private const val TAG = "FCM"
    }

    private val messaging = FirebaseMessaging.getInstance()

    override suspend fun getToken(): String? =
        suspendCancellableCoroutine { continuation ->
            messaging.token
                .addOnSuccessListener { token ->
                    Log.d(TAG, "FCM Token: $token")
                    continuation.resume(token)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to fetch FCM token", exception)
                    continuation.resume(null)
                }
        }

    override fun subscribeToTopic(topic: String) {
        messaging.subscribeToTopic(topic)
            .addOnSuccessListener {
                Log.d(TAG, "Subscribed to $topic")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed subscribing to $topic", it)
            }
    }

    override fun unsubscribeFromTopic(topic: String) {
        messaging.unsubscribeFromTopic(topic)
            .addOnSuccessListener {
                Log.d(TAG, "Unsubscribed from $topic")
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed unsubscribing from $topic", it)
            }
    }

    override suspend fun deleteToken() =
        suspendCancellableCoroutine<Unit> { continuation ->

            messaging.deleteToken()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resume(Unit)
                }
        }


    override fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}