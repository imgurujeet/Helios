package ai.achaialabs.promptr.promptrApp.ui.mapper

import ai.achaialabs.promptr.promptrApp.domain.model.User
import ai.achaialabs.promptr.promptrApp.ui.model.UserUi

fun User.toUi() : UserUi{
    return UserUi(
        id = id,
        name = name,
        avatarUrl = avatarUrl
    )

}