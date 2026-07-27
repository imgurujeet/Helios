package ai.achaialabs.helios.notifcations

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {

    fun create(context: Context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager =
            context.getSystemService(NotificationManager::class.java)

        val channels = listOf(

            NotificationChannel(
                NotificationConstants.GENERAL_CHANNEL_ID,
                NotificationConstants.GENERAL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General Helios notifications"
            },

            NotificationChannel(
                NotificationConstants.PROMOTION_CHANNEL_ID,
                NotificationConstants.PROMOTION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Offers and Premium promotions"
            },

            NotificationChannel(
                NotificationConstants.UPDATE_CHANNEL_ID,
                NotificationConstants.UPDATE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "New prompts and app updates"
            }
        )

        manager.createNotificationChannels(channels)
    }
}