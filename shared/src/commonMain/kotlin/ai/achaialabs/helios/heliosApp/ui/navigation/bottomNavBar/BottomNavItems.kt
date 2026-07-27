package ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar

import ai.achaialabs.helios.heliosApp.ui.navigation.Explore
import ai.achaialabs.helios.heliosApp.ui.navigation.Favourite
import ai.achaialabs.helios.heliosApp.ui.navigation.Home
import ai.achaialabs.helios.heliosApp.ui.navigation.Profile
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.model.BottomNavItem
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.model.NavItemIconSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_astrnt
import helios.shared.generated.resources.ic_saturn
import helios.shared.generated.resources.ic_solar_system

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
        icon = NavItemIconSource.Vector(imageVector = Icons.Default.FavoriteBorder),
        label = "Loved",
        destination = Favourite
    ),
    BottomNavItem(
        icon = NavItemIconSource.Drawable(resId = Res.drawable.ic_astrnt),
        label = "Profile",
        destination = Profile
    ),



)