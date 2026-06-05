package ai.achaialabs.helios.heliosApp.ui.mapper

import ai.achaialabs.helios.heliosApp.domain.model.User
import ai.achaialabs.helios.heliosApp.ui.model.UserUi

fun User.toUi() : UserUi{
    return UserUi(
        id = id,
        name = name,
        email = email,
        avatarUrl = avatarUrl,
        isPro = isPro ?: false
    )

}