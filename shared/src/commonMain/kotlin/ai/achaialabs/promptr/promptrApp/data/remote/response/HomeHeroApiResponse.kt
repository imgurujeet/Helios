package ai.achaialabs.promptr.promptrApp.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeHeroApiResponse(

    val id: String,

    val title: String,

    val description: String? = null,

    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("action_type")
    val actionType: String,

    @SerialName("action_value")
    val actionValue: String,

    @SerialName("start_time")
    val startTime: String? = null,

    @SerialName("end_time")
    val endTime: String? = null
)