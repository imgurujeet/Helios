package ai.achaialabs.helios.heliosApp.domain.usecase

import ai.achaialabs.helios.heliosApp.data.remote.service.SubscriptionManager
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetPremiumStatusUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isProFlow
    }
}