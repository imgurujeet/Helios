package ai.achaialabs.promptr.promptrApp.ui.navigation

import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.IsLoggedInUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.LogoutUseCase
import ai.achaialabs.promptr.promptrApp.ui.explore.ExploreScreen
import ai.achaialabs.promptr.promptrApp.ui.home.HomeScreen
import ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.BottomNavBar
import ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.ScrollAwareBottomBar
import ai.achaialabs.promptr.promptrApp.ui.navigation.bottomNavBar.bottomNavItem
import ai.achaialabs.promptr.promptrApp.ui.onboarding.LoginScreen
import ai.achaialabs.promptr.promptrApp.ui.onboarding.SplashScreen
import ai.achaialabs.promptr.promptrApp.ui.profile.ProfileScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavigation(
    onLogout: () -> Unit
) {
    val backStack = rememberNavBackStack(
        configuration = navConfig,
        Home
    )

    val bottomRoutes = bottomNavItem.map { it.destination }
    val chromeState = rememberChromeState()

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack
        ) { key ->
            when (key) {
                Home -> NavEntry(key) {
                    HomeScreen(chromeState = chromeState)
                }

                Explore -> NavEntry(key) {
                    ExploreScreen()
                }

                Profile -> NavEntry(key) {
                    ProfileScreen(
                        onLogout = onLogout
                    )
                }

                else -> error("Unknown key: $key")
            }
        }

        val current = backStack.lastOrNull()
        if (current in bottomRoutes) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)

            ) {
                ScrollAwareBottomBar(visible = chromeState.bottomBarVisible) {
                    BottomNavBar(backStack)
                }
            }
        }
    }
}

class ChromeState {
    var bottomBarVisible by mutableStateOf(true)
}

@Composable
fun rememberChromeState(): ChromeState {
    return remember { ChromeState() }
}
