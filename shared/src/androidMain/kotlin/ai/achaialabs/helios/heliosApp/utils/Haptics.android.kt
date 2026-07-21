package ai.achaialabs.helios.heliosApp.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

actual object Haptics {

    @RequiresPermission(Manifest.permission.VIBRATE)
    actual fun vibrateClick() {

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val manager =
                appContext.getSystemService(
                    android.content.Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            manager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")

            appContext.getSystemService(
                android.content.Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            vibrator.vibrate(

                VibrationEffect.createWaveform(
                    longArrayOf(0, 25, 15, 25),
                    intArrayOf(0, 200, 0, 200),
                    -1
                )
            )

        } else {

            @Suppress("DEPRECATION")

            vibrator.vibrate(15)
        }
    }
}