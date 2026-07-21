package ai.achaialabs.helios

import ai.achaialabs.helios.heliosApp.ad.ActivityProvider
import ai.achaialabs.helios.heliosApp.app.App
import ai.achaialabs.helios.heliosApp.di.androidModule
import ai.achaialabs.helios.heliosApp.di.appModule
import ai.achaialabs.helios.heliosApp.utils.Haptics
import ai.achaialabs.helios.heliosApp.utils.appContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

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
        enableEdgeToEdge()

        MobileAds.initialize(this) { initializationStatus ->
            // SDK is ready
        }
        super.onCreate(savedInstanceState)
        appContext = this


        appContext = applicationContext


        setContent {
            App(
                appDeclaration = {
                    androidContext(this@MainActivity)
                    modules(appModule,androidModule) // Loads the Android Okio Path
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}