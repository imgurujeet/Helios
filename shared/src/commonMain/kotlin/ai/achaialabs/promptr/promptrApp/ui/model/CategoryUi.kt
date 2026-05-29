package ai.achaialabs.promptr.promptrApp.ui.model

data class CategoryUi(

    val id: String,

    val name: String,

    val imageUrl: String?,

    val iconUrl: String?,

    val isSelected: Boolean = false
)