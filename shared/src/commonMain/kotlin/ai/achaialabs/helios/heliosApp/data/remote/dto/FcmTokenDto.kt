package ai.achaialabs.helios.heliosApp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenDto(
    val user_id: String,
    val token: String,
    val platform: String,
    val updated_at: String? = null
)