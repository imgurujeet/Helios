package ai.achaialabs.helios.heliosApp.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosExternalAppLauncher : ExternalAppLauncher {
    override fun launch(packageId: String, fallbackUrl: String) {
        // On iOS, we rely entirely on the URL.
        // If the URL is "https://chatgpt.com", iOS will open the ChatGPT app if installed,
        // or Safari if it isn't!
        val nsUrl = NSURL(string = fallbackUrl)
        if (UIApplication.sharedApplication.canOpenURL(nsUrl)) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }
}

@Composable
actual fun rememberExternalAppLauncher(): ExternalAppLauncher {
    return remember { IosExternalAppLauncher() }
}