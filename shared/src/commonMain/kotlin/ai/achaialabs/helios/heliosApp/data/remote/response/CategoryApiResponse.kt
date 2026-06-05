package ai.achaialabs.helios.heliosApp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryApiResponse(

    val id: String,

    val name: String,

    @SerialName("icon_url")
    val iconUrl: String? = null,

    @SerialName("image_url")
    val imageUrl: String? = null,

    @SerialName("is_premium")
    val isPremium: Boolean = false
)