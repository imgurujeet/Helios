package ai.achaialabs.helios.heliosApp.ui.promptDetail.components

import ai.achaialabs.helios.heliosApp.domain.model.FeedMedia
import ai.achaialabs.helios.heliosApp.domain.model.Prompt
import ai.achaialabs.helios.heliosApp.ui.media.MediaRenderer
import ai.achaialabs.helios.heliosApp.ui.model.FeedMediaUi
import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicAccent
import ai.achaialabs.helios.heliosApp.ui.promptDetail.CosmicDarkBg
import ai.achaialabs.helios.heliosApp.ui.promptDetail.GlassBorder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import helios.shared.generated.resources.Res
import helios.shared.generated.resources.ic_star_orbit
import org.jetbrains.compose.resources.painterResource
import kotlin.math.round

@Composable
fun HeroImageSection(
    currentPrompt: Prompt,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onFullScreenClick: () -> Unit = {}
) {
    val formattedLikes = remember(currentPrompt.stats.likesCount) {
        val count = currentPrompt.stats.likesCount
        if (count >= 1000) {
            val rounded = round((count / 1000.0) * 10) / 10.0
            val text = if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
            "${text}k"
        } else {
            count.toString()
        }
    }

    val mediaUi = remember(currentPrompt.media) {
        when (val media = currentPrompt.media) {
            is FeedMedia.Image -> FeedMediaUi.Image(media.imageUrl, media.aspectRatio)
            is FeedMedia.Video -> FeedMediaUi.Video(
                videoUrl = media.videoUrl,
                thumbnailUrl = media.thumbnailUrl,
                durationText = media.durationMs.toString(),
                aspectRatio = media.aspectRatio
            )
            else -> error("Unsupported media type")
        }
    }

    val isVideo = mediaUi is FeedMediaUi.Video

    // 🚀 FIX: Starts as TRUE so it waits for the user to tap before playing!
    var manuallyPaused by remember { mutableStateOf(true) }

    // Resets back to paused when they swipe away
    LaunchedEffect(isPlaying) {
        if (!isPlaying) manuallyPaused = true
    }

    val effectiveIsPlaying = isPlaying && !manuallyPaused

    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isVideo) manuallyPaused = !manuallyPaused
                onPlayClick()
            }
    ) {

        MediaRenderer(
            media = mediaUi,
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, GlassBorder, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp)),
            isPlaying = effectiveIsPlaying,

            onPlayClick = null,

            //  UPGRADED COSMIC PLAY BUTTON
            customPlayButton = if (isVideo && !effectiveIsPlaying) {
                {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)), // Slightly darker backdrop to make the button pop
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp) // Larger, more inviting touch target
                                .clip(CircleShape)
                                .background(
                                    // Deep space glassmorphism
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.25f),
                                            CosmicDarkBg.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.5.dp,
                                    // Shiny cosmic rim light effect
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.8f),
                                            CosmicAccent.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(44.dp)
                                    .padding(start = 4.dp) // Play icons need a slight right-shift to look perfectly centered optically
                            )
                        }
                    }
                }
            } else null
        )

        // Gradient Vignette
//        Box(
//            modifier = Modifier
//                .matchParentSize()
//                .clip(RoundedCornerShape(28.dp))
//                .background(
//                    Brush.verticalGradient(
//                        0.0f to CosmicDarkBg.copy(alpha = 0.35f),
//                        0.4f to Color.Transparent,
//                        0.7f to CosmicDarkBg.copy(alpha = 0.30f),
//                        1.0f to CosmicDarkBg.copy(alpha = 0.95f)
//                    )
//                )
//        )

        // Pro Badge
        if (currentPrompt.isPremium) {
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .border(1.dp, CosmicAccent.copy(alpha = 0.6f), RoundedCornerShape(100.dp)),
                shape = RoundedCornerShape(100.dp),
                color = CosmicDarkBg.copy(alpha = 0.65f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_star_orbit),
                        contentDescription = "Premium",
                        tint = CosmicAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CosmicAccent
                        )
                    )
                }
            }
        }

        // Bottom Content
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {

            Text(
                text = currentPrompt.content.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CosmicMediaAction(
                    icon = if (currentPrompt.interactions.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    tint = if (currentPrompt.interactions.isLiked) CosmicAccent else Color.White,
                    label = formattedLikes,
                    onClick = onLikeClick
                )
                CosmicMediaAction(
                    icon = Icons.Rounded.Share,
                    tint = Color.White,
                    onClick = onShareClick
                )
//                CosmicMediaAction(
//                    icon = Icons.Rounded.Fullscreen,
//                    tint = Color.White,
//                    onClick = onFullScreenClick
//                )
            }
        }
    }
}

// Reusable Glassy Action Component
@Composable
fun CosmicMediaAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String? = null,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CosmicDarkBg.copy(alpha = 0.55f))
                .border(1.dp, GlassBorder, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }

        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}