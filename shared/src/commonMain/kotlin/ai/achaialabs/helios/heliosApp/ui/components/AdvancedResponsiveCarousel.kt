package ai.achaialabs.helios.heliosApp.ui.components

import ai.achaialabs.helios.heliosApp.utils.Haptics
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign

enum class CarouselLayoutMode {
    CENTER_FOCUSED,
    ASYMMETRIC_START
}

data class CustomCarouselConfig(
    val layoutMode: CarouselLayoutMode = CarouselLayoutMode.CENTER_FOCUSED,
    val height: Dp = 220.dp,
    val pageSpacing: Dp = 8.dp,
    val outerPadding: Dp = 8.dp,
    val mainWidthRatio: Float = 0.80f,
    val minorWidthRatio: Float = 0.10f,
    // ==========================================
    // NEW: Indicator Configuration
    // ==========================================
    val showIndicator: Boolean = false,
    val indicatorSpacing: Dp = 16.dp,
    val shapeProvider: (isFocused: Boolean, sizeFraction: Float) -> Shape = { _, _ -> RoundedCornerShape(24.dp) }
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> AdvancedResponsiveCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    config: CustomCarouselConfig = CustomCarouselConfig(),
    onItemClick: ((T) -> Unit)? = null,
    content: @Composable BoxScope.(item: T, isFocused: Boolean) -> Unit
) {
    if (items.isEmpty()) return

    val startPage = if (config.layoutMode == CarouselLayoutMode.CENTER_FOCUSED) items.lastIndex / 2 else 0
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { items.size })

    // ANTI-GLITCH AUTO-SYNC
    var lastPage by remember { mutableIntStateOf(startPage) }

    LaunchedEffect(pagerState.settledPage) {
        val newPage = pagerState.settledPage
        if (newPage != lastPage) {
            lastPage = newPage
            Haptics.vibrateClick()
        }
    }

    // Prevents the "double swipe" bug by bouncing the pager back if the user flings past the logical end.
    val safeLastIndexInt = (items.size - 2).coerceAtLeast(0)
    LaunchedEffect(pagerState.settledPage) {
        if (config.layoutMode == CarouselLayoutMode.ASYMMETRIC_START) {
            if (pagerState.settledPage > safeLastIndexInt) {
                pagerState.animateScrollToPage(safeLastIndexInt)
            }
        }
    }

    // ==========================================
    // NEW: Wrapper Column for Carousel + Indicator
    // ==========================================
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The core carousel logic remains exactly the same
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val density = LocalDensity.current
            val maxPx = with(density) { maxWidth.toPx() }

            val pageSpacingPx = with(density) { config.pageSpacing.toPx() }
            val outerPaddingPx = with(density) { config.outerPadding.toPx() }
            val horizontalPadding = with(density) { outerPaddingPx.toDp() }

            val totalCardSpacePx = maxPx - (2 * outerPaddingPx) - (2 * pageSpacingPx)
            val mainWidthPx = totalCardSpacePx * config.mainWidthRatio
            val minorWidthPx = totalCardSpacePx * config.minorWidthRatio

            HorizontalPager(
                state = pagerState,
                pageSpacing = config.pageSpacing,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                beyondViewportPageCount = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(config.height)
            ) { page ->
                val p = pagerState.currentPage.toFloat() + pagerState.currentPageOffsetFraction
                val pageOffset = p - page.toFloat()
                val absoluteOffset = pageOffset.absoluteValue

                val cardWidthPx: Float
                val finalTranslationX: Float
                val isFocused: Boolean

                // ==========================================
                // ENGINE 1: CENTER FOCUSED (Home)
                // ==========================================
                if (config.layoutMode == CarouselLayoutMode.CENTER_FOCUSED) {
                    isFocused = (page == pagerState.currentPage)

                    cardWidthPx = lerp(
                        start = minorWidthPx, stop = mainWidthPx, fraction = 1f - absoluteOffset.coerceIn(0f, 1f)
                    )

                    val nativeSlotWidthPx = maxPx - (2 * outerPaddingPx) + pageSpacingPx
                    val nativeX = absoluteOffset * nativeSlotWidthPx

                    val targetX = if (absoluteOffset == 0f) 0f
                    else if (absoluteOffset <= 1f) absoluteOffset * ((mainWidthPx / 2f) + pageSpacingPx + (minorWidthPx / 2f))
                    else {
                        val posAtOne = (mainWidthPx / 2f) + pageSpacingPx + (minorWidthPx / 2f)
                        posAtOne + (absoluteOffset - 1f) * (minorWidthPx + pageSpacingPx)
                    }

                    val direction = sign(pageOffset)
                    val baseTranslation = direction * (nativeX - targetX)

                    val lastIndex = items.lastIndex.toFloat()
                    val leftAnchorShift = if (p < 1f) (p - 1f) * minorWidthPx else 0f
                    val rightAnchorShift = if (p > lastIndex - 1f) (p - (lastIndex - 1f)) * minorWidthPx else 0f

                    finalTranslationX = baseTranslation + leftAnchorShift + rightAnchorShift
                }

                // ==========================================
                // ENGINE 2: ASYMMETRIC START (Explore)
                // ==========================================
                else {
                    val lastIndex = items.lastIndex.toFloat()
                    val safeLastIndex = (lastIndex - 1f).coerceAtLeast(0f)

                    val overscroll = (p - safeLastIndex).coerceAtLeast(0f)
                    val rubberBand = if (overscroll > 0f) {
                        val friction = 0.5f
                        (overscroll * friction) / (overscroll * friction + 1f)
                    } else 0f

                    val mathP = if (p <= safeLastIndex) p else safeLastIndex + rubberBand

                    val currentMathPage = mathP.roundToInt()
                    isFocused = (page == currentMathPage || page == currentMathPage + 1)

                    val s = page.toFloat() - mathP + 1f

                    cardWidthPx = when {
                        s <= 0f -> minorWidthPx
                        s <= 1f -> lerp(minorWidthPx, mainWidthPx, s)
                        s <= 2f -> mainWidthPx
                        s <= 3f -> lerp(mainWidthPx, minorWidthPx, s - 2f)
                        else -> minorWidthPx
                    }

                    val pos0 = -minorWidthPx / 2f - pageSpacingPx
                    val pos1 = mainWidthPx / 2f
                    val pos2 = mainWidthPx + pageSpacingPx + mainWidthPx / 2f
                    val pos3 = mainWidthPx * 2f + pageSpacingPx * 2f + minorWidthPx / 2f
                    val pos4 = pos3 + minorWidthPx + pageSpacingPx

                    val basePos = when {
                        s <= 0f -> pos0 - (0f - s) * (minorWidthPx + pageSpacingPx)
                        s <= 1f -> lerp(pos0, pos1, s)
                        s <= 2f -> lerp(pos1, pos2, s - 1f)
                        s <= 3f -> lerp(pos2, pos3, s - 2f)
                        else -> pos4 + (s - 4f) * (minorWidthPx + pageSpacingPx)
                    }

                    val endPhase = (mathP - (safeLastIndex - 1f)).coerceIn(0f, 1f)
                    val globalShift = endPhase * (minorWidthPx + pageSpacingPx)

                    val targetX = basePos + globalShift

                    val availableW = maxPx - (2 * outerPaddingPx)
                    val nativeSlotWidthPx = availableW + pageSpacingPx
                    val nativeX = (availableW / 2f) - pageOffset * nativeSlotWidthPx

                    finalTranslationX = targetX - nativeX
                }

                val sizeFraction = ((cardWidthPx - minorWidthPx) / (mainWidthPx - minorWidthPx)).coerceIn(0f, 1f)
                val currentShape = config.shapeProvider(isFocused, sizeFraction)

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        shape = currentShape,
                        modifier = Modifier
                            .width(with(density) { cardWidthPx.toDp() })
                            .height(config.height)
                            .graphicsLayer { translationX = finalTranslationX }
                            .then(if (onItemClick != null) Modifier.clickable { onItemClick(items[page]) } else Modifier)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            content(items[page], isFocused)
                        }
                    }
                }
            }
        }

        // ==========================================
        // NEW: Page Indicator
        // ==========================================
        if (config.showIndicator) {
            Spacer(modifier = Modifier.height(config.indicatorSpacing))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(items.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration

                    // Smoothly animate the width to create a "pill" effect when selected
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        label = "indicator_width"
                    )

                    // Smoothly animate the color
                    val indicatorColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xF0D55900) else Color(0xF0D55900).copy(alpha = 0.3f),
                        label = "indicator_color"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(indicatorWidth)
                            .clip(CircleShape)
                            .background(indicatorColor)
                    )
                }
            }
        }
    }
}