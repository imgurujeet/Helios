package ai.achaialabs.helios.heliosApp.ui.mapper

import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.domain.model.Tool
import ai.achaialabs.helios.heliosApp.ui.model.PromptUi
import ai.achaialabs.helios.heliosApp.ui.model.ToolUi

fun Prompt.toUi(): PromptUi {

    return PromptUi(
        id = id,
        title = content.title,
        promptText = content.promptText,
        description = content.description,
        media = media.toUi(),
        authorName = author.name,
        authorAvatarUrl = author.avatarUrl,
        categoryName = category.name,
        likesCount = stats.likesCount,
        isLiked = interactions.isLiked,
        createdAtText = metadata.createdAt.toString(),
        isPremium= isPremium
    )
}

fun Tool.toUi(): ToolUi {
    return ToolUi(
        id = id,
        name = name,
        iconUrl = iconUrl
    )
}