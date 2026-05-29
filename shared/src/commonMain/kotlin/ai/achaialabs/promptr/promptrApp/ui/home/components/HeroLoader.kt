package ai.achaialabs.promptr.promptrApp.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CosmicHeroLoader(
    modifier: Modifier = Modifier,
) {
    val accent = Color(0xF0D55900)

    val transition = rememberInfiniteTransition(label = "loader")

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B1020)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF08090D),
                                Color(0xFF050507)
                            )
                        )
                    )
            ) {

                // COSMIC BACK GLOW
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.18f),
                                    Color.Transparent
                                ),
                                center = Offset(900f, 100f),
                                radius = 1000f
                            )
                        )
                )

                // STARS
                repeat(18) { index ->

                    val starAlpha = if (index % 2 == 0) 0.8f else 0.4f

                    Box(
                        modifier = Modifier
                            .offset(
                                x = remember(index) {
                                    (-180..180).random().dp
                                },
                                y = remember(index) {
                                    (-120..120).random().dp
                                }
                            )
                            .size(
                                if (index % 3 == 0)
                                    3.dp
                                else
                                    2.dp
                            )
                            .clip(CircleShape)
                            .background(
                                if (index % 4 == 0)
                                    accent.copy(alpha = starAlpha)
                                else
                                    Color.White.copy(alpha = starAlpha)
                            )
                    )
                }

                // MOVING UFO
                val ufoOffset by transition.animateFloat(
                    initialValue = -140f,
                    targetValue = 140f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 4200,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ufo"
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(
                            x = ufoOffset.dp,
                            y = (-20).dp
                        )
                ) {

                    // UFO GLOW
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        accent.copy(alpha = 0.18f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // SCAN LIGHT
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 40.dp)
                            .width(90.dp)
                            .height(120.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        accent.copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // UFO BODY
                    Box(
                        modifier = Modifier
                            .size(width = 92.dp, height = 38.dp)
                            .clip(RoundedCornerShape(100))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF2A2A2E),
                                        Color(0xFF45454F),
                                        Color(0xFF2A2A2E)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                accent.copy(alpha = 0.20f),
                                RoundedCornerShape(100)
                            )
                            .align(Alignment.Center)
                    )

                    // UFO TOP
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-12).dp)
                            .size(width = 42.dp, height = 24.dp)
                            .clip(RoundedCornerShape(100))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        accent.copy(alpha = 0.9f),
                                        accent.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )

                    // LIGHTS
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        repeat(4) {

                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                            )
                        }
                    }
                }

                // LOADING TEXT
                Text(
                    text = "Scanning the cosmic archive...",
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(22.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White.copy(alpha = 0.42f)
                )
            }

        }
    }
}