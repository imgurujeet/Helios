package ai.achaialabs.helios.heliosApp.ui.profile.components

import ai.achaialabs.helios.heliosApp.ui.profile.heliosGold
import ai.achaialabs.helios.heliosApp.ui.profile.heliosOrange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_star_orbit
import org.jetbrains.compose.resources.painterResource
import kotlin.random.Random

private data class StarParticle(val x: Float, val y: Float, val radius: Float, val alpha: Float)

@Composable
@Preview
fun ProCard(modifier: Modifier = Modifier,onUpgradeClick: () -> Unit = {}) {

    // Generate random star dust particles just once using remember
    val starCount = 45
    val starParticles = remember {
        List(starCount) {
            StarParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 0.5f, // Sizes between 0.5 and 3.0
                alpha = Random.nextFloat() * 0.6f + 0.1f   // Opacity between 0.1 and 0.7
            )
        }
    }

    Card(
        modifier = modifier.fillMaxWidth().clickable(){
            onUpgradeClick()
        },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
        ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    heliosGold.copy(alpha = 0.5f),
                    heliosOrange.copy(alpha = 0.1f),
                   // Color.Transparent
                )
            )
        )
    ) {
        // Box allows us to layer the background effects BEHIND the text
        Box(modifier = Modifier.fillMaxWidth()) {

            // 1. NEBULA GLOW BACKGROUND
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                heliosOrange.copy(alpha = 0.15f),
                                heliosGold.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            radius = 400f
                        )
                    )
            )

            // 2. STAR DUST CANVAS
            Canvas(modifier = Modifier.matchParentSize()) {
                starParticles.forEach { star ->
                    drawCircle(
                        color = heliosGold.copy(alpha = star.alpha),
                        radius = star.radius,
                        center = Offset(
                            x = size.width * star.x,
                            y = size.height * star.y
                        )
                    )
                }
            }

            // 3. MAIN CONTENT
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ICON BACKGROUND WITH GLOW BORDER
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    heliosGold.copy(alpha = 0.2f),
                                    heliosOrange.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(heliosGold.copy(alpha = 0.5f), Color.Transparent)
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_star_orbit),
                        contentDescription = null,
                        tint = heliosGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Upgrade to Helios Pro",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        // Apply a subtle gradient to the text itself to make it pop
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Unlock premium prompts and exclusive visuals.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }

            }
        }
    }
}