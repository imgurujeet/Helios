package ai.achaialabs.helios.heliosApp.domain.usecase.auth

import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {

    operator fun invoke(): Flow<User?> {
        return repository.getCurrentUser()
    }
}