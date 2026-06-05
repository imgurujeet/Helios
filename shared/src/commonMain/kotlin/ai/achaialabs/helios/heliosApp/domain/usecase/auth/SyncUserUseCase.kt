package ai.achaialabs.helios.heliosApp.domain.usecase.auth

import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository

class SyncUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> = repository.syncUser()
}
