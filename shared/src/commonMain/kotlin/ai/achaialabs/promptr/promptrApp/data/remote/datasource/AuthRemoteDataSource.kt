package ai.achaialabs.promptr.promptrApp.data.remote.datasource

import ai.achaialabs.promptr.promptrApp.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken

interface AuthRemoteDataSource {
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun getCurrentUser(): Result<User>
}

class AuthRemoteDataSourceImpl(
    private val supabaseClient: SupabaseClient
) : AuthRemoteDataSource {
    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            supabaseClient.auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
            }
            
            getCurrentUser()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val userInfo = supabaseClient.auth.retrieveUserForCurrentSession()
            println(
                "USER META = ${userInfo.userMetadata}"
            )
            Result.success(
                User(
                    id = userInfo.id,
                    name =
                        userInfo.userMetadata
                            ?.get("full_name")
                            ?.toString()
                            ?.replace("\"", "")
                            ?: userInfo.userMetadata
                                ?.get("name")
                                ?.toString()
                                ?.replace("\"", "")
                            ?: "",

                    avatarUrl =
                        userInfo.userMetadata
                            ?.get("avatar_url")
                            ?.toString()
                            ?.replace("\"", "")
                            ?: userInfo.userMetadata
                                ?.get("picture")
                                ?.toString()
                                ?.replace("\"", ""),
                    email = userInfo.email ?: "",
                    token = supabaseClient.auth.currentAccessTokenOrNull() ?: ""
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
