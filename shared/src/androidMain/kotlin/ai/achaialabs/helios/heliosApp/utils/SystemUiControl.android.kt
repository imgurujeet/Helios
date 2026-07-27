package ai.achaialabs.helios.heliosApp.utils

import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat


actual object SystemUiController {

    private var activity: ComponentActivity? = null

    fun initialize(activity: ComponentActivity) {
        this.activity = activity
    }

    actual fun setDarkIcons(darkIcons: Boolean) {
        activity?.let {
            WindowCompat.getInsetsController(it.window, it.window.decorView)
                .isAppearanceLightStatusBars = darkIcons
        }
    }
}