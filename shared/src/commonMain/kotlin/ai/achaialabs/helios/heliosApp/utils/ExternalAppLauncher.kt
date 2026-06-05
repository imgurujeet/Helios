package ai.achaialabs.helios.heliosApp.utils

import androidx.compose.runtime.Composable

interface ExternalAppLauncher {
    // packageId is for Android (e.g., "com.openai.chatgpt")
    // urlScheme is for iOS (e.g., "chatgpt://") or web fallback
    fun launch(packageId: String, fallbackUrl: String,promptText: String)
}

@Composable
expect fun rememberExternalAppLauncher(): ExternalAppLauncher