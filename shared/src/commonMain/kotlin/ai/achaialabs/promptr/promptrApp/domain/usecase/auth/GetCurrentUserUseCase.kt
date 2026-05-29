package ai.achaialabs.promptr.promptrApp.domain.usecase.auth

import ai.achaialabs.promptr.promptrApp.domain.model.User
import ai.achaialabs.promptr.promptrApp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {

    operator fun invoke(): Flow<User?> {
        return repository.getCurrentUser()
    }
}