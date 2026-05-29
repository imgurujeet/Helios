package ai.achaialabs.promptr.promptrApp.data.remote.mapper

import ai.achaialabs.promptr.promptrApp.data.remote.dto.UserDto
import ai.achaialabs.promptr.promptrApp.data.remote.response.UserApiResponse

fun UserApiResponse.toDto(): UserDto {
    return UserDto(
        id = id,
        name = name,
        avatarUrl = avatarUrl
    )
}