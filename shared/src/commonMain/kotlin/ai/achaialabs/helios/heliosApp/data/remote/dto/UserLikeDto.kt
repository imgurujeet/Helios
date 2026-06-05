package ai.achaialabs.helios.heliosApp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLikeDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("prompt_id") val promptId: String
)