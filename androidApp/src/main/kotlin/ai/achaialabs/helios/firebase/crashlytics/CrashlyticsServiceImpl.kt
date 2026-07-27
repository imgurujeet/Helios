package ai.achaialabs.helios.firebase.crashlytics

import ai.achaialabs.helios.heliosApp.firebase.crashlytics.CrashlyticsService
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsServiceImpl : CrashlyticsService {

    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }

    override fun setCrashlyticsEnabled(enabled: Boolean) {
        crashlytics.setCrashlyticsCollectionEnabled(enabled)
    }
}