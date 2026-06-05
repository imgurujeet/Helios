package ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar

import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.model.NavItemIconSource
import ai.achaialabs.helios.heliosApp.utils.Haptics
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.painterResource


@Composable
fun BottomNavBar(
    backStack: MutableList<NavKey>
){
    LocalRippleConfiguration provides null

    Column(
        modifier = Modifier
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                      //  0.12f to MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                        0.32f to MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                        0.5f to MaterialTheme.colorScheme.surface,
                        1f to MaterialTheme.colorScheme.surface
                    )
                )
            )
            //.background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(
            thickness = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier
                .padding(horizontal = 16.dp)

        )
        NavigationBar(
            modifier = Modifier.height(60.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            bottomNavItem.forEach { item ->
                val selected = backStack.last() == item.destination
                CompositionLocalProvider(
                    LocalRippleConfiguration provides null
                ) {

                    NavigationBarItem(
                        selected = selected,
                        //enabled = false,
                        // onClick = {},

                        onClick = {
                            if (!selected) {
                                Haptics.vibrateClick()
                                backStack.clear()
                                backStack.add(item.destination)
                            }
                        },

                        alwaysShowLabel = false,
                        icon = {

                            when (val icon = item.icon) {
                                is NavItemIconSource.Vector -> {
                                    Icon(
                                        imageVector = icon.imageVector,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected) Color(0xF0D55900) else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.8f
                                        )
                                    )
                                }

                                is NavItemIconSource.Drawable -> {
                                    Icon(
                                        painter = painterResource(icon.resId),
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected) Color(0xF0D55900) else MaterialTheme.colorScheme.onBackground.copy(
                                            alpha = 0.8f
                                        )
                                    )
                                }


                            }

                        },

                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.background,
                            selectedTextColor = MaterialTheme.colorScheme.onBackground,
                            unselectedTextColor = Color.Black,
                            unselectedIconColor = Color.Black
                        )
                    )
                }

            }
        }
    }
}


@Composable
fun ScrollAwareBottomBar(
    modifier: Modifier = Modifier,
    visible: Boolean,
    content: @Composable () -> Unit
) {

    var bottomBarHeight by remember {
        mutableStateOf(0f)
    }

    val translationY by animateFloatAsState(
        targetValue =
            if (visible) 0f else bottomBarHeight,

        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),

        label = "bottomBarTranslation"
    )

    val alpha by animateFloatAsState(
        targetValue =
            if (visible) 1f else 0f,

        animationSpec = tween(250),

        label = "bottomBarAlpha"
    )

    Box(
        modifier = modifier
            .onGloballyPositioned {

                bottomBarHeight =
                    it.size.height.toFloat()
            }
            .graphicsLayer {

                this.translationY = translationY
                this.alpha = alpha
            }
    ) {

        content()
    }
}