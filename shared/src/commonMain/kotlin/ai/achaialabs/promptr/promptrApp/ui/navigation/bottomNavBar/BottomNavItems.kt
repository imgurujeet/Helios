package ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar

import ai.achaialabs.promptr.promptrApp.ui.navigation.Explore
import ai.achaialabs.promptr.promptrApp.ui.navigation.Home
import ai.achaialabs.promptr.promptrApp.ui.navigation.Profile
import ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.model.BottomNavItem
import ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.model.NavItemIconSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import promptr.shared.generated.resources.Res
import promptr.shared.generated.resources.ic_astrnt
import promptr.shared.generated.resources.ic_astronaut
import promptr.shared.generated.resources.ic_saturn
import promptr.shared.generated.resources.ic_solar_system
import promptr.shared.generated.resources.ic_ufo

val bottomNavItem = listOf(

    BottomNavItem(
        icon = NavItemIconSource.Drawable(resId = Res.drawable.ic_saturn),
        label = "Home",
        destination = Home
    ),
    BottomNavItem(
        icon = NavItemIconSource.Drawable(resId = Res.drawable.ic_solar_system),
        label = "Orders",
        destination = Explore

    ),
    BottomNavItem(
        icon = NavItemIconSource.Drawable(resId = Res.drawable.ic_astrnt),
        label = "Profile",
        destination = Profile
    ),

)