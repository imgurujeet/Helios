package ai.achaialabs.helios.heliosApp.data.mapper

import ai.achaialabs.helios.heliosApp.data.local.entity.CategoryEntity
import ai.achaialabs.helios.heliosApp.domain.model.PromptCategory

// --- Category Mappers ---

fun CategoryEntity.toDomain(): PromptCategory {
    return PromptCategory(
        id = id,
        name = name,
        imageUrl = imageUrl,
        iconUrl = iconUrl,
        isPremium = isPremium
    )
}

fun PromptCategory.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        iconUrl = iconUrl,
        isPremium = isPremium

    )
}

