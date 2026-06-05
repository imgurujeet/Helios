package ai.achaialabs.helios.heliosApp.ui.media.player

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
actual fun PlatformVideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean,
    isPreview: Boolean,
    onReady: () -> Unit
) {

    val context = LocalContext.current

    val exoPlayer = remember(url) {

        ExoPlayer.Builder(context).build().apply {

            setMediaItem(
                MediaItem.fromUri(
                    Uri.parse(url)
                )
            )

            prepare()

            repeatMode = if (isPreview) {
                Player.REPEAT_MODE_OFF
            } else {
                Player.REPEAT_MODE_ONE
            }

            volume = if (isPreview) {
                0f
            } else {
                1f
            }
        }
    }

    LaunchedEffect(autoPlay) {

        if(autoPlay) {
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    LaunchedEffect(isPreview) {

        while (isPreview) {

            delay(200)

            if (exoPlayer.currentPosition >= 3000) {
                exoPlayer.seekTo(0)
            }
        }
    }

    DisposableEffect(exoPlayer) {

        val listener = object : Player.Listener {

            override fun onRenderedFirstFrame() {
                onReady()
            }
        }

        exoPlayer.addListener(listener)

        onDispose {

            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->

            PlayerView(ctx).apply {

                player = exoPlayer

                useController = false
                controllerAutoShow = false
                hideController()

                resizeMode =
                    AspectRatioFrameLayout.RESIZE_MODE_ZOOM

                setShutterBackgroundColor(
                    android.graphics.Color.TRANSPARENT
                )

                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = modifier
    )
}