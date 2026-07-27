package ai.achaialabs.helios

import ai.achaialabs.helios.heliosApp.ad.ActivityProvider
import ai.achaialabs.helios.heliosApp.app.App
import ai.achaialabs.helios.heliosApp.di.androidModule
import ai.achaialabs.helios.heliosApp.di.appModule
import ai.achaialabs.helios.heliosApp.utils.SystemUiController
import ai.achaialabs.helios.heliosApp.utils.appContext
import ai.achaialabs.helios.notifcations.NotificationChannels
import ai.achaialabs.helios.notifcations.NotificationPermissionHandler
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.messaging.messaging
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {


    private lateinit var notificationPermissionLauncher:
            ActivityResultLauncher<String>

    override fun onResume() {
        super.onResume()
        ActivityProvider.currentActivity = this
    }
    override fun onPause() {
        super.onPause()

        if (ActivityProvider.currentActivity == this) {
            ActivityProvider.currentActivity = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        MobileAds.initialize(this) { initializationStatus ->
            // SDK is ready
        }

        super.onCreate(savedInstanceState)

        notificationPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                Log.d("Permission", "Granted = $granted")
            }

        appContext = this

        SystemUiController.initialize(this)
        appContext = applicationContext

        NotificationChannels.create(this)

        setContent {

            App(
                appDeclaration = {
                    androidContext(this@MainActivity)
                    modules(appModule,androidModule,firebaseModule) // Loads the Android Okio Path
                },
                onRequestNotificationPermission = {
                    requestNotificationPermission()
                }
            )
        }
    }

    fun requestNotificationPermission() {
        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

