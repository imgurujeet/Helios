package ai.achaialabs.helios.heliosApp.domain.usecase.auth

import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> = repository.loginWithGoogle(idToken)
}
