package ai.achaialabs.promptr.promptrApp.ui.home.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp

@Composable
fun LoadingPromptCard(
    modifier: Modifier = Modifier
) {

    val accent = Color(0xF0D55900)

    val transition = rememberInfiniteTransition(label = "loading")

    val shimmerTranslate by transition.animateFloat(
        initialValue = -300f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1400,
                easing = LinearEasing
            )
        ),
        label = "shimmer"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.03f),
            accent.copy(alpha = 0.14f),
            Color.White.copy(alpha = 0.03f)
        ),
        start = Offset(shimmerTranslate, shimmerTranslate),
        end = Offset(
            shimmerTranslate + 300f,
            shimmerTranslate + 300f
        )
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF09090C)
        ),
        border = BorderStroke(
            1.dp,
            accent.copy(alpha = 0.08f)
        )
    ) {

        Column {

            // HERO AREA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF111114),
                                Color(0xFF09090C)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                // ATMOSPHERE GLOW
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .scale(pulse)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.20f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // ORBIT RING
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .border(
                            1.dp,
                            accent.copy(alpha = 0.18f),
                            CircleShape
                        )
                )

                // CORE
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    accent.copy(alpha = 0.9f),
                                    accent.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .background(shimmerBrush),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(30.dp)
                    )
                }

                // STARS
                repeat(8) { index ->

                    Box(
                        modifier = Modifier
                            .offset(
                                x = remember(index) {
                                    (-120..120).random().dp
                                },
                                y = remember(index) {
                                    (-80..80).random().dp
                                }
                            )
                            .size(
                                if (index % 2 == 0)
                                    3.dp
                                else
                                    2.dp
                            )
                            .clip(CircleShape)
                            .background(
                                if (index % 3 == 0)
                                    accent.copy(alpha = 0.8f)
                                else
                                    Color.White.copy(alpha = 0.5f)
                            )
                    )
                }
            }

            // CONTENT
            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .height(18.dp)
                        .clip(RoundedCornerShape(100))
                        .background(shimmerBrush)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            accent.copy(alpha = 0.08f)
                        )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            accent.copy(alpha = 0.08f)
                        )
                )
            }
        }
    }
}
