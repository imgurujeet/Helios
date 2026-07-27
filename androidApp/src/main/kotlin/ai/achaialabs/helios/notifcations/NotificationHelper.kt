package ai.achaialabs.helios.notifcations

import ai.achaialabs.helios.MainActivity
import android.Manifest
import android.R
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

object NotificationHelper {

    fun show(
        context: Context,
        message: RemoteMessage
    ) {

        val title =
            message.notification?.title
                ?: message.data["title"]
                ?: "Helios"

        val body =
            message.notification?.body
                ?: message.data["body"]
                ?: ""

        val intent = Intent(
            context,
            MainActivity::class.java
        ).apply {

            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP

            // Forward all FCM data
            message.data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                Random.nextInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                NotificationConstants.GENERAL_CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification_overlay)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context)
            .notify(
                Random.nextInt(),
                notification
            )
    }
}