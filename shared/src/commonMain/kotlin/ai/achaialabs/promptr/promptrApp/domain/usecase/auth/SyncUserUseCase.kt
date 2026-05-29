package ai.achaialabs.promptr.promptrApp.domain.usecase.auth

import ai.achaialabs.promptr.promptrApp.domain.model.User
import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository

class SyncUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> = repository.syncUser()
}
