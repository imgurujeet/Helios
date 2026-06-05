package ai.achaialabs.helios.heliosApp.ui.media

import ai.achaialabs.helios.heliosApp.ui.media.player.PlatformVideoPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage


@Composable
fun VideoMedia(
    videoUrl: String,
    thumbnail: String?,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlayClick: (() -> Unit)? = null,
    customPlayButton: (@Composable () -> Unit)? = null

) {

    var isVideoReady by remember(isPlaying) {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier
    ) {

        // VIDEO PLAYER

        if (isPlaying) {

            PlatformVideoPlayer(
                url = videoUrl,
                modifier = Modifier.fillMaxSize(),
                autoPlay = true,
                isPreview = true,
                onReady = {
                    isVideoReady = true
                }
            )
        }

        // THUMBNAIL

        AnimatedVisibility(
            visible = !isPlaying || !isVideoReady,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            if (thumbnail != null) {

                AsyncImage(
                    model = thumbnail,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

            } else {

                VideoThumbnail(
                    videoUrl = videoUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (customPlayButton != null) {

            // If the parent passed a custom button, draw it exactly as they designed it!
            customPlayButton()

        } else if (onPlayClick != null) {

            // 2. 🚀 FIX: Only draw the fallback button if they actually provided a click action!
            // Because of the 'if' check above, Kotlin "smart casts" onPlayClick to be non-null here!
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        }
                    ) {
                        // This is now 100% safe!
                        onPlayClick()
                    },
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.45f)
            ) {
                Icon(
                    imageVector = if (!isPlaying) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(18.dp)
                )
            }
        }
    }

}

@Composable
expect fun VideoThumbnail(
    videoUrl: String,
    modifier: Modifier = Modifier
)


