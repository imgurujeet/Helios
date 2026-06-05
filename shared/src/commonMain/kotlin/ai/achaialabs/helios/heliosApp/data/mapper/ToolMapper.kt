package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.ToolEntity
import ai.achaialabs.helios.heliosApp.data.remote.dto.ToolDto
import ai.achaialabs.helios.heliosApp.domain.model.Tool

fun Tool.toEntity(): ToolEntity {
    return ToolEntity(
        id = id,
        name = name,
        iconUrl = iconUrl
    )

}

fun ToolEntity.toDomain(): Tool {
    return Tool(
        id = id,
        name = name,
        iconUrl = iconUrl
    )
}