package ai.achaialabs.promptr.promptrApp.ui.mapper

import ai.achaialabs.promptr.promptrApp.domain.model.Prompt
import ai.achaialabs.promptr.promptrApp.ui.model.PromptUi

fun Prompt.toUi(): PromptUi {

    return PromptUi(
        id = id,
        title = content.title,
        description = content.description,
        media = media.toUi(),
        authorName = author.name,
        authorAvatarUrl = author.avatarUrl,
        categoryName = category.name,
        likesCount = stats.likesCount,
        isLiked = interactions.isLiked,
        createdAtText = metadata.createdAt.toString()
    )
}