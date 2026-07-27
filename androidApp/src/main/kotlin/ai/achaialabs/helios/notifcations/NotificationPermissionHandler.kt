package ai.achaialabs.helios.notifcations

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class NotificationPermissionHandler(
    private val activity: ComponentActivity
) {

    private var onResult: ((Boolean) -> Unit)? = null

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            onResult?.invoke(granted)
        }

    fun isGranted(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(
        callback: (Boolean) -> Unit
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            callback(true)
            return
        }

        if (isGranted()) {
            callback(true)
            return
        }

        onResult = callback

        launcher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}