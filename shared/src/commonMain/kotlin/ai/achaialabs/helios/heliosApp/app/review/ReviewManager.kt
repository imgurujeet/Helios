package ai.achaialabs.helios.heliosApp.app.review


sealed interface ReviewResult {

    data object Completed : ReviewResult

    data object NotAvailable : ReviewResult

    data class Error(
        val throwable: Throwable
    ) : ReviewResult
}
interface AppReviewManager {

    suspend fun launchReview(): ReviewResult
}