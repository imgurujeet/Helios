package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.datasource.AuthLocalDataSource
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.datasource.AuthRemoteDataSource
import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import com.revenuecat.purchases.kmp.Purchases
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val localDataSource: AuthLocalDataSource,
    private val remoteDataSource: AuthRemoteDataSource,
    private val supabaseClient: SupabaseClient,
    private val crashlytics: CrashlyticsService
) : AuthRepository {

    override val isProFlow: Flow<Boolean> =
        localDataSource.getCurrentUser()
            .map { userEntity ->
                userEntity?.isPro ?: false
            }

    override fun getCurrentUser(): Flow<User?> {
        return localDataSource.getCurrentUser()
            .map { it?.toDomain() }
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): Result<User> = withContext(Dispatchers.IO) {

        val result = remoteDataSource.loginWithGoogle(idToken)
        result.onFailure { e ->
            crashlytics.log("Google login failed")
            crashlytics.recordException(e)
        }
        if (result.isSuccess) {

            val user = result.getOrThrow()
            crashlytics.setUserId(user.id)
            // Save locally
            localDataSource.saveUser(user.toEntity())

            try {
                // IMPORTANT: Link RevenueCat user
                Purchases.sharedInstance.logIn(
                    newAppUserID = user.id,
                    onSuccess = { customerInfo, created ->
                        println("RevenueCat login success")
                    },
                    onError = { error ->
                        println("RevenueCat login failed: ${error.message}")
                        crashlytics.log("RevenueCat login failed: ${error.message}")
                        crashlytics.recordException(Exception("RevenueCat login failed: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                crashlytics.recordException(e)
            }
        }

        result
    }

    override suspend fun syncUser(): Result<User> =
        withContext(Dispatchers.IO) {

            val result = remoteDataSource.getCurrentUser()
            result.onFailure { e ->
                crashlytics.log("syncUser failed")
                crashlytics.recordException(e)
            }
            if (result.isSuccess) {

                val user = result.getOrThrow()
                crashlytics.setUserId(user.id)
                // Save locally
                localDataSource.clearUser()
                localDataSource.saveUser(user.toEntity())

                try {
                    // IMPORTANT:
                    // restore RevenueCat identity after app restart
                    Purchases.sharedInstance.logIn(
                        newAppUserID = user.id,
                        onSuccess = { customerInfo, created ->
                            println("RevenueCat login success")
                        },
                        onError = { error ->
                            crashlytics.log("RevenueCat restore login failed")
                            crashlytics.recordException(Exception(error.message))
                        }
                    )
                } catch (e: Exception) {
                    crashlytics.recordException(e)
                }
            }

            result
        }

    override suspend fun logout() =
        withContext(Dispatchers.IO) {

            try {
                Purchases.sharedInstance.logOut(
                    onSuccess = {
                        println("RC logout success")
                    },
                    onError = { error ->
                        error.message
                        crashlytics.log("RevenueCat logout failed")
                        crashlytics.recordException(Exception(error.message))
                    }
                )
            } catch (e: Exception) {
                crashlytics.recordException(e)
            }

            supabaseClient.auth.signOut()

            localDataSource.clearUser()
        }

    override suspend fun isLoggedIn(): Boolean =
        withContext(Dispatchers.IO) {

            val session =
                supabaseClient.auth.currentSessionOrNull()

            println("SUPABASE SESSION: $session")

            session != null
        }

    override suspend fun updateProStatus(
        isPro: Boolean
    ) = withContext(Dispatchers.IO) {

        val currentUser = localDataSource.getCurrentUserSync() ?: return@withContext

        // Nothing changed → don't update Room or Supabase
        if (currentUser.isPro == isPro) {
            println("Premium unchanged. Skipping update.")
            return@withContext
        }

        println("Premium changed: ${currentUser.isPro} -> $isPro")

        val updatedUser = currentUser.copy(
            isPro = isPro
        )
        crashlytics.setCustomKey("user_id", currentUser.id)
        crashlytics.setCustomKey("is_pro", isPro.toString())

        // Update Room
        localDataSource.saveUser(updatedUser)

        try {
            supabaseClient
                .from("profiles")
                .update({
                    set("is_pro", isPro)
                }) {
                    filter {
                        eq("id", currentUser.id)
                    }
                }

            println("Supabase updated.")

        } catch (e: Exception) {
            crashlytics.log("Failed updating premium status")

            crashlytics.setCustomKey("is_pro", isPro.toString())

            crashlytics.recordException(e)
        }
    }
}