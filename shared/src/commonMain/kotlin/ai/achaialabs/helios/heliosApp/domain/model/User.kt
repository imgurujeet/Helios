package ai.achaialabs.helios.heliosApp.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val token: String? = null,
    val isPro: Boolean ?= false
)
