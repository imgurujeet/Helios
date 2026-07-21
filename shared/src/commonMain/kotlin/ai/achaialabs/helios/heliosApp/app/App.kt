package ai.achaialabs.helios.heliosApp.app

import ai.achaialabs.helios.heliosApp.di.appModule
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.IsLoggedInUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.LogoutUseCase
import ai.achaialabs.helios.heliosApp.ui.navigation.AppNavigation
import ai.achaialabs.helios.heliosApp.ui.onboarding.LoginScreen
import ai.achaialabs.helios.heliosApp.ui.onboarding.SplashScreen
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.KoinAppDeclaration
import ai.achaialabs.helios.BuildKonfig
import ai.achaialabs.helios.heliosApp.ad.AdManager

sealed class AppState {
    data object Loading : AppState()
    data object Authenticated : AppState()
    data object Unauthenticated : AppState()
}
@Preview
@Composable
fun App(
    appDeclaration: KoinAppDeclaration = {}
) {


    KoinApplication(application = { appDeclaration()
        modules(appModule) }) {

        val viewModel: MainViewModel = koinInject() // Inject the MainViewModel
        val appState by viewModel.appState.collectAsState()
        LaunchedEffect(appState) {
            println("UI APP STATE = $appState")
        }
        val userThemePreference by viewModel.isDarkTheme.collectAsState()
        val systemTheme = isSystemInDarkTheme()

        // Use user preference if it exists, otherwise fallback to system default
        val darkTheme = userThemePreference ?: systemTheme
        // 1. Initialize SDKs once
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory()) }
                .build()
        }

        LaunchedEffect(Unit) {

            if (!Purchases.isConfigured) {

                Purchases.configure(
                    PurchasesConfiguration.Builder(
                        BuildKonfig.REVENUECAT_API_KEY
                    ).build()
                )
            }
        }

        MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = appState,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { state ->

                    println("CURRENT SCREEN STATE = $state")
                    when (state) {
                        AppState.Loading -> SplashScreen(onSplashFinished = { /* Optional trigger */ })

                        AppState.Unauthenticated -> LoginScreen(onLoginSuccess = {
                            viewModel.onLoginSuccess()
                        })

                        AppState.Authenticated -> AppNavigation(onLogout = {
                            viewModel.onLogout()
                        })
                    }
                }
            }
        }
    }
}
