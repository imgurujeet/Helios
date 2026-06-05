package ai.achaialabs.helios.heliosApp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ToolApiResponse (
    val id: String,
    val name: String,
    @SerialName("icon_url")
    val iconUrl: String? = null
)
