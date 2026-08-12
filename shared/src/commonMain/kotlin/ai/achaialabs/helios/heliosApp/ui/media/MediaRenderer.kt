package ai.achaialabs.helios.heliosApp.ui.media

import ai.achaialabs.helios.heliosApp.ui.model.FeedMediaUi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MediaRenderer(
    media: FeedMediaUi,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlayClick: (() -> Unit)? = null,
    customPlayButton: (@Composable () -> Unit)? = null
) {

    when(media) {

        is FeedMediaUi.Image -> {

            ImageMedia(
                url = media.imageUrl,
                aspectRatio = media.aspectRatio,
                modifier = modifier,

            )
        }

        is FeedMediaUi.Video -> {

            VideoMedia(
                videoUrl = media.videoUrl,
                thumbnail = media.thumbnailUrl,
                aspectRatio = media.aspectRatio,
                modifier = modifier,
                isPlaying = isPlaying,
                onPlayClick = onPlayClick,
                customPlayButton = customPlayButton
            )
        }
    }
}