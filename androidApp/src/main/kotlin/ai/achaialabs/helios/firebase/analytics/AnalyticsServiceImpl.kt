package ai.achaialabs.helios.firebase.analytics

import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class AnalyticsServiceImpl : AnalyticsService {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    override fun logEvent(
        name: String,
        params: Map<String, Any?>
    ) {
        val bundle = Bundle()

        params.forEach { (key, value) ->
            when (value) {
                null -> Unit

                is String -> bundle.putString(key, value)

                is Int -> bundle.putInt(key, value)

                is Long -> bundle.putLong(key, value)

                is Double -> bundle.putDouble(key, value)

                is Float -> bundle.putDouble(key, value.toDouble())

                is Boolean -> bundle.putBoolean(key, value)

                is Short -> bundle.putInt(key, value.toInt())

                is Byte -> bundle.putInt(key, value.toInt())

                is Char -> bundle.putString(key, value.toString())

                is Number -> bundle.putLong(key, value.toLong())

                else -> bundle.putString(key, value.toString())
            }
        }

        analytics.logEvent(name, bundle)
    }

    override fun setUserId(userId: String?) {
        analytics.setUserId(userId)
    }

    override fun setUserProperty(
        key: String,
        value: String?
    ) {
        analytics.setUserProperty(key, value)
    }

    override fun reset() {
        analytics.resetAnalyticsData()
    }
}