package ai.achaialabs.promptr.promptrApp.domain.model

data class Prompt(

    val id: String,

    val content: PromptContent,

    val author: PromptAuthor,

    val media: FeedMedia,

    val category: PromptCategory,

    val stats: PromptStats,

    val interactions: PromptInteractions,

    val metadata: PromptMetadata
)

data class PromptContent(

    val title: String,

    val description: String? = null
)

data class PromptAuthor(

    val id: String,

    val name: String,

    val avatarUrl: String? = null
)

data class PromptStats(

    val likesCount: Int = 0,

    val commentsCount: Int = 0,

    val sharesCount: Int = 0
)

data class PromptInteractions(

    val isLiked: Boolean = false,

    val isBookmarked: Boolean = false
)

data class PromptMetadata(

    val tags: List<String> = emptyList(),

    val createdAt: Long
)