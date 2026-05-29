package ai.achaialabs.promptr

import ai.achaialabs.promptr.promptrApp.di.appModule
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.IsLoggedInUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.LogoutUseCase
import ai.achaialabs.promptr.promptrApp.ui.navigation.AppNavigation
import ai.achaialabs.promptr.promptrApp.ui.onboarding.LoginScreen
import ai.achaialabs.promptr.promptrApp.ui.onboarding.SplashScreen
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
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

sealed class AppState {
    data object Loading : AppState()
    data object Authenticated : AppState()
    data object Unauthenticated : AppState()
}

@Composable
@Preview
fun App() {

    KoinApplication(configuration = koinConfiguration(declaration = {
        modules(
            appModule
        )
    }), content = {
        val darkTheme = isSystemInDarkTheme()
        val scope = rememberCoroutineScope()
        val isLoggedInUseCase: IsLoggedInUseCase = koinInject()
        val logoutUseCase: LogoutUseCase = koinInject()

        var appState by remember { mutableStateOf<AppState>(AppState.Loading) }

        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components {
                    add(KtorNetworkFetcherFactory())
                }
                .build()
        }

        MaterialTheme(
            colorScheme =
            if (darkTheme) darkColorScheme()
            else lightColorScheme()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = appState,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { state ->
                    when (state) {
                        AppState.Loading -> {
                            SplashScreen(
                                onSplashFinished = {
                                    scope.launch {
                                        if (isLoggedInUseCase()) {
                                            appState = AppState.Authenticated
                                        } else {
                                            appState = AppState.Unauthenticated
                                        }
                                    }
                                }
                            )
                        }

                        AppState.Unauthenticated -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    appState = AppState.Authenticated
                                }
                            )
                        }

                        AppState.Authenticated -> {
                            AppNavigation(
                                onLogout = {
                                    scope.launch {
                                        logoutUseCase()
                                        appState = AppState.Unauthenticated
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

    })

}
