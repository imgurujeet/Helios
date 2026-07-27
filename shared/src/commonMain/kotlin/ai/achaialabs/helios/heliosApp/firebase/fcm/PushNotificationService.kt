package ai.achaialabs.helios.heliosApp.firebase.fcm

interface PushNotificationService {

    suspend fun getToken(): String?

    fun subscribeToTopic(topic: String)

    fun unsubscribeFromTopic(topic: String)

    suspend fun deleteToken()

    fun isPermissionGranted(): Boolean
}