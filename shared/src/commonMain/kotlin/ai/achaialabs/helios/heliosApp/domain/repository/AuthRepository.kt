package ai.achaialabs.helios.heliosApp.domain.repository

import ai.achaialabs.helios.heliosApp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun syncUser(): Result<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean

    suspend fun updateProStatus(isPro: Boolean)
}
