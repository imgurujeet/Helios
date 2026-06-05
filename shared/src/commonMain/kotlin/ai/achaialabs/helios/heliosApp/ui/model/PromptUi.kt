package ai.achaialabs.helios.heliosApp.ui.model

data class PromptUi(

    val id: String,

    val title: String,

    val description: String?,
    val promptText: String,
    val media: FeedMediaUi,

    val authorName: String,

    val authorAvatarUrl: String?,

    val categoryName: String,

    val likesCount: Int,

    val isLiked: Boolean,

    val createdAtText: String,
    val isPremium: Boolean,
    val recommendedTools: ToolUi ?= null,

)

data class ToolUi(
    val id: String,
    val name: String,
    val iconUrl: String
)