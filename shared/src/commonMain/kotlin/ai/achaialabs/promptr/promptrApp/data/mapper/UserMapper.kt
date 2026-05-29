package ai.achaialabs.promptr.promptrApp.data.mapper

import ai.achaialabs.promptr.promptrApp.data.local.entity.UserEntity
import ai.achaialabs.promptr.promptrApp.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        token = token
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        token = token
    )
}
