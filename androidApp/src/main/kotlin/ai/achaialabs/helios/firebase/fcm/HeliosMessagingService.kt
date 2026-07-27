package ai.achaialabs.helios.firebase.fcm

import ai.achaialabs.helios.notifcations.NotificationHelper
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HeliosMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        NotificationHelper.show(
            context = this,
            message = message
        )
        Log.d("FCM", message.data.toString())
    }
}