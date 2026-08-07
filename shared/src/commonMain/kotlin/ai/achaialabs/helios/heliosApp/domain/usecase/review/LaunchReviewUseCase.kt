package ai.achaialabs.helios.heliosApp.domain.usecase.review

import ai.achaialabs.helios.heliosApp.app.review.AppReviewManager
import ai.achaialabs.helios.heliosApp.app.review.ReviewResult

class LaunchReviewUseCase(
    private val reviewManager: AppReviewManager
) {
    suspend operator fun invoke(): ReviewResult {
        return reviewManager.launchReview()
    }
}