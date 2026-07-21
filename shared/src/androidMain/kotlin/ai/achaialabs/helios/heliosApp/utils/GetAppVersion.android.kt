package ai.achaialabs.helios.heliosApp.utils

import android.content.Context

lateinit var appContext: Context

actual fun getAppVersion(): String {
    return try {
        val packageInfo = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        "v${packageInfo.versionName}"
    } catch (e: Exception) {
        "Unknown"
    }
}