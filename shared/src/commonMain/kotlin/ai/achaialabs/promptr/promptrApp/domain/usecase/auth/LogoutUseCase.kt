package ai.achaialabs.promptr.promptrApp.domain.usecase.auth

import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logout()
}
