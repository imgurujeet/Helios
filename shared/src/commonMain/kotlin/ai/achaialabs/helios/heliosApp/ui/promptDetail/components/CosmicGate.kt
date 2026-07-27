package ai.achaialabs.helios.heliosApp.ui.promptDetail.components

import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicAccent
import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicDarkBg
import ai.achaialabs.helios.heliosApp.ui.promptDetail.GlassBorder
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_gift_box
import helios.shared.generated.resources.ic_star_orbit
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CosmicGate(
    isPremium: Boolean,
    isPro: Boolean,
    isRevealed: Boolean,
    isAdLoading: Boolean = false,
    onRevealClick: () -> Unit,
    onSubscribeClick: () -> Unit,
    content: @Composable () -> Unit
) {

    val canAccess = when {
        isPro -> true
        isPremium -> false
        else -> isRevealed
    }

//    // High-converting, low-friction marketing copy
//    val titleText = when {
//        isPremium -> "UNLIMITED ACCESS"
//        else -> "INSTANT ACCESS"
//    }

    val buttonText = when {
        isPremium -> "GO PRO"
        else -> "VIEW PROMPT"
    }

//    val subtitleText = when {
//        isPremium ->
//            "Unlimited access to all premium prompts."
//        else ->
//            "Instant access to this prompt."
//    }

    val footerText = when {
        isPremium ->
            "Zero Ads • Infinite Access • Cancel Anytime"
        else ->
            "Quick Ad • Instant Access" // Transparency reduces ad drop-offs
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        // MAIN CONTENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    if (!canAccess) {
                        alpha = 0.35f
                    }
                }
        ) {
            content()
        }

        // LOCK OVERLAY
        if (!canAccess) {

            // BLOCK ALL TOUCHES
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    }
            )

            // VISUAL OVERLAY - Adjusted gradient for much better text visibility
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to MaterialTheme.colorScheme.surface.copy(alpha = 0.20f),
                                0.4f to MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
                                1f to MaterialTheme.colorScheme.surface
                            )
                        )
                    ),
                contentAlignment = Alignment.TopCenter
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp).padding(top = 60.dp)
                        //.offset(y = (20).dp)
                ) {

//                    Text(
//                        text = titleText,
//                        style = MaterialTheme.typography.labelMedium.copy(
//                            letterSpacing = 2.sp,
//                            fontWeight = FontWeight.Black
//                        ),
//                        color = CosmicAccent
//                    )
//
//                    Spacer(modifier = Modifier.height(10.dp))
//
//                    Text(
//                        text = subtitleText,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.90f), // Slightly boosted text alpha for readability
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(18.dp))


                    Button(
                        enabled = !isAdLoading,

                        onClick = {
                            if (isPremium) {
                                onSubscribeClick()
                            } else {
                                onRevealClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CosmicAccent,
                            contentColor = CosmicDarkBg
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        if (isAdLoading) {
                            //CircularWavyProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xF0D55900),)
                            LoadingIndicator(modifier = Modifier.size(20.dp), color = Color(0xF0D55900))
                        } else {
                            Text(buttonText)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            painter= if(isPremium) painterResource(Res.drawable.ic_star_orbit) else painterResource(Res.drawable.ic_gift_box),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = footerText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}