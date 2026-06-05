package ai.achaialabs.helios.heliosApp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidExternalAppLauncher(private val context: Context) : ExternalAppLauncher {
    override fun launch(packageId: String, fallbackUrl: String, promptText: String) {

        // ATTEMPT 1: Direct Share Injection
        // We try to send the text directly to the app as a "Share" action.
        // If the app supports it, it will open and pre-fill the text box!
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, promptText)
            setPackage(packageId) // 🚀 FORCE it to go to this specific app
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(shareIntent)
            return // Success! It launched and (hopefully) injected.
        } catch (e: ActivityNotFoundException) {
            // The app is either not installed, OR it doesn't allow direct sharing.
            // Move on to Attempt 2.
        }

        // ATTEMPT 2: Standard Launch (Fallback)
        // The app is installed, but doesn't accept injected text. We just open it normally.
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageId)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return
        }

        // ATTEMPT 3: Web Fallback
        // The app is not installed at all. Open the browser.
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
        webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(webIntent)
    }
}

@Composable
actual fun rememberExternalAppLauncher(): ExternalAppLauncher {
    val context = LocalContext.current
    return remember(context) { AndroidExternalAppLauncher(context) }
}