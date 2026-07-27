package ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar

import ai.achaialabs.helios.heliosApp.ui.navigation.Home
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.model.NavItemIconSource
import ai.achaialabs.helios.heliosApp.utils.Haptics
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
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
                                while (
                                    backStack.isNotEmpty() &&
                                    backStack.last() !is Home
                                ) {
                                    backStack.removeAt(backStack.lastIndex)
                                }

                                if (backStack.isEmpty() || backStack.last() != item.destination) {
                                    backStack.add(item.destination)
                                }
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
fun BottomNavBarFloating(
    backStack: MutableList<NavKey>
) {

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
        contentAlignment = Alignment.BottomCenter
    ) {


        Row(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth(0.62f)
                .height(60.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(100.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(100.dp))
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {

            bottomNavItem.forEach { item ->

                val selected = backStack.last() == item.destination

                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.15f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = ""
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .clickable {

                            if (!selected) {
                                Haptics.vibrateClick()
                                while (
                                    backStack.isNotEmpty() &&
                                    backStack.last() !is Home
                                ) {
                                    backStack.removeAt(backStack.lastIndex)
                                }

                                if (backStack.isEmpty() || backStack.last() != item.destination) {
                                    backStack.add(item.destination)
                                }
                            }

                        },
                    contentAlignment = Alignment.Center
                ) {

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