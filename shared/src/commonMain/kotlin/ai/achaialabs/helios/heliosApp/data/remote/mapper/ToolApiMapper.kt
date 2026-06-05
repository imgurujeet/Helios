package ai.achaialabs.helios.heliosApp.data.remote.mapper

import ai.achaialabs.helios.heliosApp.data.remote.dto.ToolDto
import ai.achaialabs.helios.heliosApp.data.remote.response.ToolApiResponse

fun ToolApiResponse.toDto(): ToolDto {
    return ToolDto(
        id = id,
        name = name,
        iconUrl = iconUrl
    )
}
