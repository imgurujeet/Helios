package ai.achaialabs.helios.heliosApp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data object Explore : NavKey

@Serializable
data object Profile : NavKey


@Serializable
data class ViewAll(val categoryId: String, val categoryName: String) : NavKey

@Serializable
data class PromptDetail(val promptId: String,val categoryId: String? = null) : NavKey