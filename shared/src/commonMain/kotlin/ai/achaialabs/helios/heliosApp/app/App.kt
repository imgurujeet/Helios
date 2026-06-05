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

sealed class AppState {
    data object Loading : AppState()
    data object Authenticated : AppState()
    data object Unauthenticated : AppState()
}
@Preview
@Composable
fun App() {
    KoinApplication(application = { modules(appModule) }) {

        val viewModel: MainViewModel = koinInject() // Inject the MainViewModel
        val appState by viewModel.appState.collectAsState()
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

        // 2. Initialize RevenueCat globally once
        Purchases.configure(PurchasesConfiguration.Builder("test_wKdc...").build())

        MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = appState,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { state ->
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
//fun App() {
//
//    // 1. Simplified Koin Application initialization
//    KoinApplication(
//        application = {
//            modules(appModule)
//        }
//    ) {
//
//
//        val supabase = koinInject<SupabaseClient>()
//        val isLoggedInUseCase: IsLoggedInUseCase = koinInject()
//        val logoutUseCase: LogoutUseCase = koinInject()
//
//        val scope = rememberCoroutineScope()
//        var appState by remember { mutableStateOf<AppState>(AppState.Loading) }
//        var isPremium by remember { mutableStateOf(false) } // Track premium status
//        val darkTheme = isSystemInDarkTheme()
//
//        // 2. Coil Setup
//        setSingletonImageLoaderFactory { context ->
//            ImageLoader.Builder(context)
//                .components { add(KtorNetworkFetcherFactory()) }
//                .build()
//        }
//
//        // 3. Initialize RevenueCat & Observe Status
//        val isRCConfigured = remember { mutableStateOf(false) }
//
//        LaunchedEffect(Unit) {
//            if (!isRCConfigured.value) {
//                val apiKey = "test_wKdcERNUCUoobyfItROgCxzndOf"
//                try {
//                    Purchases.configure(PurchasesConfiguration.Builder(apiKey).build())
//                    isRCConfigured.value = true
//                } catch (e: Exception) {
//                    println("RC Config Error: ${e.message}")
//                }
//            }
//        }
//
//        fun syncRevenueCat(userId: String) {
//            Purchases.sharedInstance.logIn(
//                newAppUserID = userId,
//                onError = { println("RC Error: ${it.message}") },
//                onSuccess = { _, created -> println("RC Synced! Created: $created") }
//            )
//        }
//
//        MaterialTheme(
//            colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
//        ) {
//            Box(modifier = Modifier.fillMaxSize()) {
//                AnimatedContent(
//                    targetState = appState,
//                    transitionSpec = { fadeIn() togetherWith fadeOut() }
//                ) { state ->
//                    when (state) {
//                        AppState.Loading -> {
//                            SplashScreen(onSplashFinished = {
//                                scope.launch {
//                                    if (isLoggedInUseCase()) {
//                                        supabase.auth.currentUserOrNull()?.id?.let { syncRevenueCat(it) }
//                                        appState = AppState.Authenticated
//                                    } else {
//                                        appState = AppState.Unauthenticated
//                                    }
//                                }
//                            })
//                        }
//                        AppState.Unauthenticated -> {
//                            LoginScreen(onLoginSuccess = {
//                                supabase.auth.currentUserOrNull()?.id?.let { syncRevenueCat(it) }
//                                appState = AppState.Authenticated
//                            })
//                        }
//                        AppState.Authenticated -> {
//                            AppNavigation(
//                                onLogout = {
//                                    scope.launch {
//                                        // 1. Log out from your backend/Supabase
//                                        logoutUseCase()
//
//                                        // 2. Log out from RevenueCat with required callbacks
//                                        Purchases.sharedInstance.logOut(
//                                            onError = { error ->
//                                                println("RC Logout Error: ${error.message}")
//                                                // You can still proceed to unauthenticated state even if RC fails
//                                                appState = AppState.Unauthenticated
//                                            },
//                                            onSuccess = {
//                                                println("RC Logged out successfully")
//                                                appState = AppState.Unauthenticated
//                                            }
//                                        )
//                                    }
//                                }
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
