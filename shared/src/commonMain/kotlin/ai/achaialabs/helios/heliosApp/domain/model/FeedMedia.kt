package ai.achaialabs.helios.heliosApp.domain.model

sealed interface FeedMedia {

    val id: String

    data class Image(
        override val id: String,
        val imageUrl: String,
        val aspectRatio: Float?
    ) : FeedMedia

    data class Video(
        override val id: String,
        val videoUrl: String,
        val thumbnailUrl: String?,
        val durationMs: Long?,
        val aspectRatio: Float?
    ) : FeedMedia
}


enum class HomeFeedType {

    LATEST,

    POPULAR
}