package ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.DrawableResource

/**
 * Data class representing a navigation item in the bottom bar.
 * @param icon The primary icon to display for this navigation item
 * @param selectedIcon Optional icon to display when the item is selected
 * @param label The text label for this navigation item
 * @param route The navigation route associated with this item
 * @param badgeCount Optional badge count to display on the icon
 */
data class BottomNavItem(
    val icon: NavItemIconSource,
    val selectedIcon: NavItemIconSource? = null,
    val label: String,
    val destination: NavKey,
    val badgeCount: Int? = null
)

/**
 * Drawable resource-based icon.
 * @param resId The DrawableResource reference
 */
sealed class NavItemIconSource {
    data class Vector(val imageVector: ImageVector) : NavItemIconSource()
    data class Drawable(val resId: DrawableResource) : NavItemIconSource()
}
