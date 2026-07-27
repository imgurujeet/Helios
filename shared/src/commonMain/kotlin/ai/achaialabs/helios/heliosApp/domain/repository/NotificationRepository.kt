package ai.achaialabs.helios.heliosApp.domain.repository

interface NotificationRepository {

    suspend fun updateFcmToken(
        token: String
    )
}