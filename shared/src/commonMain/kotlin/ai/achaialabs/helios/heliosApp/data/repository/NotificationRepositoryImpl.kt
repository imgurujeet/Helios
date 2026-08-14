package ai.achaialabs.helios.heliosApp.data.repository

import ai.achaialabs.helios.heliosApp.data.remote.dto.FcmTokenDto
import ai.achaialabs.helios.heliosApp.domain.repository.AuthRepository
import ai.achaialabs.helios.heliosApp.domain.repository.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.first

class NotificationRepositoryImpl(
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository
) : NotificationRepository {

    override suspend fun updateFcmToken(token: String) {

        val user = authRepository
            .getCurrentUser()
            .first() ?: return

        supabase
            .from("user_fcm_tokens")
            .delete {
                filter {
                    eq("token", token)
                }
            }

        supabase
            .from("user_fcm_tokens")
            .upsert(
                FcmTokenDto(
                    user_id = user.id,
                    token = token,
                    platform = "android"
                )

            ) {
                onConflict = "token"
            }
    }
}