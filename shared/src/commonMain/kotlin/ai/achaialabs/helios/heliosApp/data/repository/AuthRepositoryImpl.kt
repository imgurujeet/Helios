package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.datasource.AuthLocalDataSource
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.datasource.AuthRemoteDataSource
import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource,
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> {
        return localDataSource.getCurrentUser().map { it?.toDomain() }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.loginWithGoogle(idToken)
        if (result.isSuccess) {
            val user = result.getOrThrow()
            localDataSource.saveUser(user.toEntity())
        }
        result
    }

    override suspend fun syncUser(): Result<User> = withContext(Dispatchers.IO) {
        val result = remoteDataSource.getCurrentUser()
        if (result.isSuccess) {
            val user = result.getOrThrow()
            localDataSource.saveUser(user.toEntity())
        }
        result
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        supabaseClient.auth.signOut()
        localDataSource.clearUser()
    }

    override suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        localDataSource.getCurrentUserSync() != null
    }

    override suspend fun updateProStatus(isPro: Boolean): Unit = withContext(Dispatchers.IO) {
        val currentUser = localDataSource.getCurrentUserSync()
        currentUser?.let { userEntity ->
            // This updates Room, and because of your Flow, the UI will update automatically
            localDataSource.saveUser(userEntity.copy(isPro = isPro))
        }
    }
}
