package ai.achaialabs.promptr.promptrApp.ui.mapper

import ai.achaialabs.promptr.promptrApp.domain.model.PromptCategory
import ai.achaialabs.promptr.promptrApp.ui.model.CategoryUi

fun PromptCategory.toUi(): CategoryUi {

    return CategoryUi(
        id = id,
        name = name,
        imageUrl = imageUrl,
        iconUrl = iconUrl
    )
}