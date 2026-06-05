package ai.achaialabs.helios.heliosApp.ui.model

data class UserUi(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val isPro: Boolean
)