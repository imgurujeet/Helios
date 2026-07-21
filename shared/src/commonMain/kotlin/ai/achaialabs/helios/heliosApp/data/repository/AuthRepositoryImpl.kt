package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.local.datasource.AuthLocalDataSource
import ai.achaialabs.helios.heliosApp.data.mapper.toDomain
import ai.achaialabs.helios.heliosApp.data.mapper.toEntity
import ai.achaialabs.helios.heliosApp.data.remote.datasource.AuthRemoteDataSource
import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
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
    private val supabaseClient: SupabaseClient
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

        if (result.isSuccess) {

            val user = result.getOrThrow()

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
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        result
    }

    override suspend fun syncUser(): Result<User> =
        withContext(Dispatchers.IO) {

            val result = remoteDataSource.getCurrentUser()

            if (result.isSuccess) {

                val user = result.getOrThrow()

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
                            println("RevenueCat login failed: ${error.message}")
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
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
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
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
    ): Unit = withContext(Dispatchers.IO) {

        println("========== UPDATE PRO STATUS ==========")
        println("Incoming isPro: $isPro")

        val currentUser =
            localDataSource.getCurrentUserSync()

        println("Local Room user: $currentUser")

        currentUser?.let { userEntity ->

            println("Current user id: ${userEntity.id}")
            println("Current Room isPro: ${userEntity.isPro}")

            // Update Room
            val updatedUser =
                userEntity.copy(
                    isPro = isPro
                )

            localDataSource.saveUser(updatedUser)

            println("Room updated successfully")
            println("New Room isPro: ${updatedUser.isPro}")

            try {

                println("Updating Supabase profile...")

                supabaseClient
                    .from("profiles")
                    .update(
                        {
                            set("is_pro", isPro)
                        }
                    ) {
                        filter {
                            eq("id", userEntity.id)
                        }
                    }

                println("Supabase updated successfully")
                println("Supabase is_pro => $isPro")

            } catch (e: Exception) {

                println("SUPABASE UPDATE FAILED")
                e.printStackTrace()
            }

        } ?: run {

            println("No current user found in Room")
        }

        println("=======================================")
    }
}