package ai.achaialabs.helios.heliosApp.data.remote.mapper

import ai.achaialabs.helios.heliosApp.data.remote.dto.CategoryDto
import ai.achaialabs.helios.heliosApp.data.remote.response.CategoryApiResponse


fun CategoryApiResponse.toDto(): CategoryDto {
    return CategoryDto(
        id = id,
        name = name,
        iconUrl = iconUrl,
        imageUrl = imageUrl,
        isPremium = isPremium
    )
}