package ai.achaialabs.helios.heliosApp.ui.promptDetail.components

import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicAccent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Composable
fun PromptContentCard(
    prompt: String
) {
    var expanded by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current
    val infiniteTransition = rememberInfiniteTransition(label = "blink")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "rotation"
    )

    CosmicGlassCard {
        // Added animateContentSize for smooth expand/collapse transition
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
        ) {
            // =========================
            // TOP BAR
            // =========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Color(0xFFFF5F57),
                        Color(0xFFFEBB2E),
                        Color(0xFF28C840)
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.85f))
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "SYSTEM PROMPT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.45f)
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CosmicAccent.copy(alpha = 0.12f),
                        modifier = Modifier.clickable {
                            clipboardManager.setText(AnnotatedString(prompt))
                            copied = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = CosmicAccent,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = if (copied) "Copied" else "Copy",
                                style = MaterialTheme.typography.labelMedium,
                                color = CosmicAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // =========================
            // PROMPT BODY WITH FADE
            // =========================
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 29.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.92f),
                    maxLines = if (expanded) Int.MAX_VALUE else 4,
                    // Kept clip for clean cut-off, but ellipsis works too depending on preference
                    overflow = TextOverflow.Clip
                )

                // Gradient fade effect when collapsed
                if (!expanded) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Adjust height to control how much is faded
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        // Change this to your CosmicGlassCard's actual background color
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // =========================
            // EXPAND ICON (NO BG)
            // =========================
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand Prompt",
                    // Pulse only when collapsed to draw attention
                    tint = CosmicAccent.copy(alpha = if (expanded) 1f else pulseAlpha),
                    modifier = Modifier
                        .size(32.dp) // Slightly larger touch target
                        .graphicsLayer { rotationZ = rotation }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null // Removes the grey ripple background entirely
                        ) {
                            expanded = !expanded
                        }
                )
            }
        }
    }

    // Reset copied state after delay
    LaunchedEffect(copied) {
        if (copied) {
            delay(1800)
            copied = false
        }
    }
}