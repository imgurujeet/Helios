package ai.achaialabs.helios.heliosApp.ui.mapper

import ai.achaialabs.helios.heliosApp.domain.model.PromptCategory
import ai.achaialabs.helios.heliosApp.ui.model.CategoryUi

fun PromptCategory.toUi(): CategoryUi {

    return CategoryUi(
        id = id,
        name = name,
        imageUrl = imageUrl,
        iconUrl = iconUrl,
        isPremium= isPremium,
    )
}