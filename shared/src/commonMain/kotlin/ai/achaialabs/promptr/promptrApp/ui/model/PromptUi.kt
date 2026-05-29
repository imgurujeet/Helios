package ai.achaialabs.promptr.promptrApp.ui.model

data class PromptUi(

    val id: String,

    val title: String,

    val description: String?,

    val media: FeedMediaUi,

    val authorName: String,

    val authorAvatarUrl: String?,

    val categoryName: String,

    val likesCount: Int,

    val isLiked: Boolean,

    val createdAtText: String
)