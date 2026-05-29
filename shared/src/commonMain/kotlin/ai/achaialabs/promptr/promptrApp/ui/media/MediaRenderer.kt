package ai.achaialabs.promptr.promptrApp.ui.media

import ai.achaialabs.promptr.promptrApp.ui.model.FeedMediaUi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MediaRenderer(
    media: FeedMediaUi,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlayClick: () -> Unit = {}
) {

    when(media) {

        is FeedMediaUi.Image -> {

            ImageMedia(
                url = media.imageUrl,
                modifier = modifier
            )
        }

        is FeedMediaUi.Video -> {

            VideoMedia(
                videoUrl = media.videoUrl,
                thumbnail = media.thumbnailUrl,
                modifier = modifier,
                isPlaying = isPlaying,
                onPlayClick = onPlayClick
            )
        }
    }
}