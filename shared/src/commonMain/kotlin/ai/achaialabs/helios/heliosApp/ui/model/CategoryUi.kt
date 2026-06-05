package ai.achaialabs.helios.heliosApp.ui.model

data class CategoryUi(

    val id: String,

    val name: String,

    val imageUrl: String? = null,

    val iconUrl: String? = null,

    val isSelected: Boolean = false,
    val isPremium: Boolean
)


