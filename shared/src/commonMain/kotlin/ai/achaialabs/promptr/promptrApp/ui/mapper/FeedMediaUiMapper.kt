package ai.achaialabs.promptr.promptrApp.ui.mapper

import ai.achaialabs.promptr.promptrApp.domain.model.FeedMedia
import ai.achaialabs.promptr.promptrApp.ui.model.FeedMediaUi

fun FeedMedia.toUi(): FeedMediaUi {

    return when(this) {

        is FeedMedia.Image -> {

            FeedMediaUi.Image(
                imageUrl = imageUrl,
                aspectRatio = aspectRatio
            )
        }

        is FeedMedia.Video -> {

            FeedMediaUi.Video(
                videoUrl = videoUrl,
                thumbnailUrl = thumbnailUrl,
                durationText = durationMs?.toString(),
                aspectRatio = aspectRatio
            )
        }
    }
}