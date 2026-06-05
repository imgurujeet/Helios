package ai.achaialabs.helios.heliosApp.data.remote.mapper

import ai.achaialabs.helios.heliosApp.data.remote.dto.UserDto
import ai.achaialabs.helios.heliosApp.data.remote.response.UserApiResponse

fun UserApiResponse.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        avatarUrl = avatarUrl,
        isPro = isPro

    )
}