package ai.achaialabs.helios.heliosApp.ui.home.components

import ai.achaialabs.helios.heliosApp.ui.components.AdvancedResponsiveCarousel
import ai.achaialabs.helios.heliosApp.ui.components.CarouselLayoutMode
import ai.achaialabs.helios.heliosApp.ui.components.CustomCarouselConfig
import ai.achaialabs.helios.heliosApp.ui.model.HomeHeroUi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage


@Composable
fun HomeHero(
    heroes: List<HomeHeroUi>,
    onHeroClick: (HomeHeroUi) -> Unit
){
    AdvancedResponsiveCarousel(
        items = heroes,
        config = CustomCarouselConfig(
            layoutMode = CarouselLayoutMode.CENTER_FOCUSED, // ⬅️ Triggers the 40-40-20 layout
            height = 220.dp,
            mainWidthRatio = 0.80f,  // 40% Large cards
            minorWidthRatio = 0.10f, // 20% Pill card
            pageSpacing = 16.dp,
            outerPadding = 16.dp,
            showIndicator = true,
            indicatorSpacing = 16.dp,
            shapeProvider = { _, sizeFraction ->
                // Smoothly morphs from a 100dp Pill to a 24dp Card
                val animatedCorner = lerp(
                    start = 100.dp,
                    stop = 24.dp,
                    fraction = sizeFraction
                )
                RoundedCornerShape(animatedCorner)
            }
        ),
        onItemClick = { hero -> onHeroClick(hero) }
    ) { hero, isFocused -> // ⬅️ Catch the focus state from the new index-based engine

        HomeHeroCarouselContent(
            hero = hero ,
            isFocused = isFocused,
        )

    }
}

@Composable
private fun HomeHeroCarouselContent(
    hero: HomeHeroUi,
    isFocused: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition()

    // 🚀 CINEMATIC ZOOM
    // Starts at 1.15f (15% zoomed in) so there is ALWAYS a safe buffer for panning.
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 1.15f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    // 🚀 CINEMATIC PANNING (Up & Down)
    // Moves 16 pixels up and down. Because we are zoomed in 15%, this will never show a blank edge!
    val imageVerticalOffset by infiniteTransition.animateFloat(
        initialValue = -16f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            // Notice the duration is different than the zoom (6000ms vs 8000ms).
            // This desyncs the animations so it feels organic and continuous, not repetitive.
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AsyncImage(
            model = hero.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Apply our new safe scale and panning offset
                    scaleX = imageScale
                    scaleY = imageScale
                    translationY = imageVerticalOffset
                }
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Text Content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
                .graphicsLayer {
                    this.alpha = if (isFocused) 1f else 0f
                }
        ) {
            Text(
                text = hero.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
            )

            hero.description?.let {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
    }
}