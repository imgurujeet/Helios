package ai.achaialabs.helios.heliosApp.domain.usecase.fcm

import ai.achaialabs.helios.heliosApp.domain.repository.NotificationRepository

class UpdateFcmTokenUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(token: String) {
        repository.updateFcmToken(token)
    }
}