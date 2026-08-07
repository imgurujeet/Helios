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
import ai.achaialabs.helios.heliosApp.app.update.UpdateResult
import ai.achaialabs.helios.heliosApp.firebase.fcm.PushNotificationService
import ai.achaialabs.helios.heliosApp.utils.SystemUiController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.revenuecat.purchases.kmp.LogLevel
import kotlinx.coroutines.delay

sealed class AppState {
    data object Loading : AppState()
    data object Authenticated : AppState()
    data object Unauthenticated : AppState()
}
@Preview
@Composable
fun App(
    appDeclaration: KoinAppDeclaration = {},
    onRequestNotificationPermission: (() -> Unit)? = null
) {

    KoinApplication(application = { appDeclaration()
        modules(appModule) }) {


        val viewModel: MainViewModel = koinInject() // Inject the MainViewModel
        val pushNotificationService: PushNotificationService = koinInject()
        val appState by viewModel.appState.collectAsState()
        val updateState by viewModel.updateState.collectAsState()
        LaunchedEffect(appState) {
            println("UI APP STATE = $appState")
        }
        val userThemePreference by viewModel.isDarkTheme.collectAsState()
        val navigationStyle by viewModel.navigationStyle.collectAsState()
        val systemTheme = isSystemInDarkTheme()

        // Use user preference if it exists, otherwise fallback to system default
        val darkTheme = userThemePreference ?: systemTheme

        LaunchedEffect(darkTheme) {
            SystemUiController.setDarkIcons(!darkTheme)
        }
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

        val showNotificationDialog by
        viewModel.shouldShowNotificationPrompt.collectAsState()

        var permissionChecked by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(appState) {

            if (
                appState == AppState.Authenticated &&
                !permissionChecked
            ) {

                permissionChecked = true

                delay(2500)

                viewModel.checkNotificationPrompt(
                    permissionGranted = pushNotificationService.isPermissionGranted()
                )
            }

            if (appState == AppState.Unauthenticated) {
                permissionChecked = false
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

                        AppState.Authenticated -> AppNavigation(

                            navigationStyle = navigationStyle,
                            onLogout = {
                                viewModel.onLogout()
                            },
                            onPurchaseCompleted = {
                                viewModel.refreshSubscription()
                            },
                            onRequestNotificationPermission = {
                                viewModel.hideNotificationPrompt()
                                onRequestNotificationPermission?.invoke()
                            }
                        )
                    }
                }
            }

            PermissionDialog(
                visible = showNotificationDialog,
                title = "\uD83E\uDD1D We Don't Spam",
                description = "Get notified when fresh AI prompts, new features, and important updates are available. We'll only send notifications that matter.",
                confirmText = "Send 'Em \uD83D\uDE80",
                laterText = "\uD83E\uDD79 Convince Me Later",
                icon = Icons.Rounded.NotificationsActive,
                onDismiss = {
                    viewModel.hideNotificationPrompt()
                },
                onConfirm = {
                    viewModel.hideNotificationPrompt()
                    onRequestNotificationPermission?.invoke()
                },
                onLater = {
                    viewModel.postponeNotificationPrompt()
                }
            )


            if (updateState == UpdateResult.UpdateDownloaded) {

                AlertDialog(
                    onDismissRequest = { },

                    title = {
                        Text("Update Ready")
                    },

                    text = {
                        Text(
                            "The latest version has finished downloading. Restart now to complete the update."
                        )
                    },

                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.completeUpdate()
                            }
                        ) {
                            Text("Restart")
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = { }
                        ) {
                            Text("Later")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun PermissionDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onLater: () -> Unit,
    title: String,
    description: String,
    confirmText: String = "Continue",
    laterText: String = "Maybe Later",
    icon: ImageVector = Icons.Rounded.NotificationsActive,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF59E0B).copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B)
                    )
                ) {
                    Text(confirmText)
                }

                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF59E0B)
                    ),
                    onClick = onLater
                ) {
                    Text(laterText)
                }
            }
        }
    }
}



@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun PermissionDialogPreview() {
    MaterialTheme {
        PermissionDialog(
            visible = true,
            title = "\uD83E\uDD1D We Don't Spam",
            description = "Seriously. Just hand-picked AI prompts, major updates, and things that'll make your day a little better.",
            confirmText = "Send 'Em \uD83D\uDE80",
            laterText = "\uD83E\uDD79 Convince Me Later",
            icon = Icons.Rounded.NotificationsActive,
            onDismiss = {},
            onConfirm = {},
            onLater = {}
        )
    }
}