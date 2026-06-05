package ai.achaialabs.helios.heliosApp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserApiResponse(

    val id: String,

    val name: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("is_pro")
    val isPro: Boolean = false
)
