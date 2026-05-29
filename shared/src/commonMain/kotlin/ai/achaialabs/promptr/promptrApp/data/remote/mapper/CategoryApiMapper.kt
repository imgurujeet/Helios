package ai.achaialabs.promptr.promptrApp.data.remote.mapper

import ai.achaialabs.promptr.promptrApp.data.remote.dto.CategoryDto
import ai.achaialabs.promptr.promptrApp.data.remote.response.CategoryApiResponse


fun CategoryApiResponse.toDto(): CategoryDto {
    return CategoryDto(
        id = id,
        name = name,
        iconUrl = iconUrl,
        imageUrl = imageUrl
    )
}