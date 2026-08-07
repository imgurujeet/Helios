package ai.achaialabs.helios.heliosApp.app.review

import ai.achaialabs.helios.heliosApp.ActivityProvider
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class AppReviewManagerImpl : AppReviewManager {

    override suspend fun launchReview(): ReviewResult {

        val activity = ActivityProvider.currentActivity
            ?: return ReviewResult.NotAvailable

        return try {

            suspendCancellableCoroutine { continuation ->

                val reviewManager = ReviewManagerFactory.create(activity)

                reviewManager.requestReviewFlow()
                    .addOnSuccessListener { reviewInfo ->

                        reviewManager.launchReviewFlow(activity, reviewInfo)
                            .addOnCompleteListener {
                                if (continuation.isActive) {
                                    continuation.resume(ReviewResult.Completed)
                                }
                            }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resume(
                                ReviewResult.Error(exception)
                            )
                        }
                    }
            }

        } catch (exception: Exception) {
            ReviewResult.Error(exception)
        }
    }
}