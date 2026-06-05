package ai.achaialabs.helios.heliosApp.ui.model

sealed interface FeedMediaUi {

    data class Image(
        val imageUrl: String,
        val aspectRatio: Float?
    ) : FeedMediaUi

    data class Video(
        val videoUrl: String,
        val thumbnailUrl: String?,
        val durationText: String?,
        val aspectRatio: Float?
    ) : FeedMediaUi
}