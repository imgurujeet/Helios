package ai.achaialabs.helios.heliosApp.domain.usecase.auth

import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository

class IsLoggedInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.isLoggedIn()
}
