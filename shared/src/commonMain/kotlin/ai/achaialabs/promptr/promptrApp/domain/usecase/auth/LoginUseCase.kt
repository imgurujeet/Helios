package ai.achaialabs.promptr.promptrApp.domain.usecase.auth

import ai.achaialabs.promptr.promptrApp.domain.model.User
import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> = repository.loginWithGoogle(idToken)
}
