package ai.achaialabs.helios.heliosApp.firebase.crashlytics

interface CrashlyticsService {

    fun log(message: String)

    fun recordException(
        throwable: Throwable
    )

    fun setUserId(userId: String)

    fun setCustomKey(
        key: String,
        value: String
    )

    fun setCrashlyticsEnabled(
        enabled: Boolean
    )
}