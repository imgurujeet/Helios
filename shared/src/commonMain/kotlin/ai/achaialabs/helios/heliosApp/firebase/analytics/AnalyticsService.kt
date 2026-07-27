package ai.achaialabs.helios.heliosApp.firebase.analytics

interface AnalyticsService {

    fun logEvent(
        name: String,
        params: Map<String, Any?> = emptyMap()
    )

    fun setUserId(userId: String?)

    fun setUserProperty(
        key: String,
        value: String?
    )

    fun reset()
}