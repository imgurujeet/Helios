package ai.achaialabs.promptr.promptrApp.ui.home.components

import ai.achaialabs.promptr.promptrApp.ui.model.HomeHeroUi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import kotlin.math.sign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.AsyncImage
import kotlin.math.absoluteValue
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeHeroSection(
    heroes: List<HomeHeroUi>,
    onHeroClick: (HomeHeroUi) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = heroes.lastIndex / 2,
        pageCount = { heroes.size }
    )

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val density = LocalDensity.current
        val maxPx = with(density) { maxWidth.toPx() }

        // ==========================================
        // 1. DEFINE YOUR EXACT PIXEL SPACINGS
        // ==========================================
        val pageSpacingPx = with(density) { 8.dp.toPx() } // The gap between cards
        val outerPaddingPx = with(density) { 8.dp.toPx() } // The safe margin from screen edges

        // ==========================================
        // 2. THE HOLY GRAIL MATH
        // ==========================================
        // Total width minus the outer margins and the two gaps between the 3 visible cards
        val totalCardSpacePx = maxPx - (2 * outerPaddingPx) - (2 * pageSpacingPx)

        // Assign percentages based ONLY on the remaining safe space
        val maxCardWidthPx = totalCardSpacePx * 0.80f // Center gets 80% of available space
        val minCardWidthPx = totalCardSpacePx * 0.10f // Sides get 10% each

        // Convert back to Dp for the Modifiers
        val maxCardWidth = with(density) { maxCardWidthPx.toDp() }
        val minCardWidth = with(density) { minCardWidthPx.toDp() }
        val horizontalPadding = with(density) { outerPaddingPx.toDp() }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                // REMOVED clipToBounds() so corners never get sliced!
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    beyondViewportPageCount = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) { page ->

                    val p = pagerState.currentPage.toFloat() + pagerState.currentPageOffsetFraction
                    val pageOffset = p - page.toFloat()
                    val absoluteOffset = pageOffset.absoluteValue

                    // --- WIDTH & ALPHA ---
                    val cardWidth = lerp(
                        start = minCardWidth,
                        stop = maxCardWidth,
                        fraction = 1f - absoluteOffset.coerceIn(0f, 1f)
                    )

                    val alpha = lerp(
                        start = 1f,
                        stop = 1f,
                        fraction = 1f - absoluteOffset.coerceIn(0f, 1f)
                    )

                    // --- TRANSLATION MATH ---
                    val nativeSlotWidthPx = maxPx - (2 * outerPaddingPx) + pageSpacingPx
                    val nativeX = absoluteOffset * nativeSlotWidthPx

                    val targetX = if (absoluteOffset == 0f) {
                        0f
                    } else if (absoluteOffset <= 1f) {
                        val positionAtOne = (maxCardWidthPx / 2f) + pageSpacingPx + (minCardWidthPx / 2f)
                        absoluteOffset * positionAtOne
                    } else {
                        val positionAtOne = (maxCardWidthPx / 2f) + pageSpacingPx + (minCardWidthPx / 2f)
                        val extraOffset = absoluteOffset - 1f
                        positionAtOne + extraOffset * (minCardWidthPx + pageSpacingPx)
                    }

                    val shiftDistance = nativeX - targetX
                    val direction = sign(pageOffset)
                    val itemTranslationX = direction * shiftDistance

                    // --- EDGE ANCHORS ---
                    val lastIndex = heroes.lastIndex.toFloat()

                    val leftAnchorShift = if (p < 1f) {
                        (p - 1f) * minCardWidthPx
                    } else 0f

                    val rightAnchorShift = if (p > lastIndex - 1f) {
                        (p - (lastIndex - 1f)) * minCardWidthPx
                    } else 0f

                    val finalTranslationX = itemTranslationX + leftAnchorShift + rightAnchorShift

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        HomeHeroCarouselContent(
                            hero = heroes[page],
                            cardWidth = cardWidth,
                            alpha = alpha,
                            isFocused = absoluteOffset < 0.5f,
                            modifier = Modifier.graphicsLayer {
                                this.translationX = finalTranslationX
                            },
                            onClick = {
                                onHeroClick(heroes[page])
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCarouselContent(
    hero: HomeHeroUi,
    cardWidth: Dp,
    alpha: Float,
    isFocused: Boolean,
    modifier: Modifier = Modifier, // Accept parent's translation modifier
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()

    /*
    Cinematic image motion.
     */
    val imageScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val imageVerticalOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        // Apply the parent's translation, animate the width, and LOCK the height
        modifier = modifier
            .width(cardWidth)
            .height(220.dp)
            .graphicsLayer {
                this.alpha = alpha
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp)
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            AsyncImage(
                model = hero.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop, // Automatically crops as width shrinks!
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        /*
                        Floating image effect.
                         */
                        scaleX = imageScale
                        scaleY = imageScale
                        translationY = imageVerticalOffset
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.78f)
                            )
                        )
                    )
            )

            /*
            Only focused card shows content strongly.
             */
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
                    fontSize = 28.sp
                )

                hero.description?.let {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}