package ai.achaialabs.promptr.promptrApp.domain.usecase.auth

import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository

class IsLoggedInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.isLoggedIn()
}
