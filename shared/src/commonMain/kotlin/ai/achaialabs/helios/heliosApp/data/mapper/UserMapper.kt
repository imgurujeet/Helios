package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.UserEntity
import ai.achaialabs.helios.heliosApp.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        token = token,
        isPro = isPro
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        token = token,
        isPro = isPro ?: false
    )
}
