package ai.achaialabs.helios.heliosApp.ui.navigation

import ai.achaialabs.helios.heliosApp.data.local.NavigationStyle
import ai.achaialabs.helios.heliosApp.ui.explore.ExploreScreen
import ai.achaialabs.helios.heliosApp.ui.favourite.FavouriteScreen
import ai.achaialabs.helios.heliosApp.ui.home.HomeScreen
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.BottomNavBar
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.BottomNavBarFloating
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.ScrollAwareBottomBar
import ai.achaialabs.helios.heliosApp.ui.navigation.bottomNavBar.bottomNavItem
import ai.achaialabs.helios.heliosApp.ui.profile.ProfileScreen
import ai.achaialabs.helios.heliosApp.ui.promptDetail.PromptDetailScreen
import ai.achaialabs.helios.heliosApp.ui.search.SearchScreen
import ai.achaialabs.helios.heliosApp.ui.viewall.ViewAllScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions

@Composable
fun AppNavigation(
    navigationStyle: NavigationStyle,
    onLogout: () -> Unit,
    onPurchaseCompleted: () -> Unit,
    onRequestNotificationPermission: (() -> Unit)? = null
) {
    val backStack = rememberNavBackStack(
        configuration = navConfig,
        Home
    )

    val bottomRoutes = bottomNavItem.map { it.destination }
    val chromeState = rememberChromeState(
        navigateTo = { route -> backStack.add(route) }
    )


    var showPaywall by remember {
        mutableStateOf(false)
    }
    val goBack: () -> Unit = {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack
        ) { key ->
            when (key) {
                Home -> NavEntry(key) {
                    HomeScreen(chromeState = chromeState, onProIconClick = {
                        showPaywall = true
                    })
                }

                Explore -> NavEntry(key) {
                    ExploreScreen(
                        chromeState = chromeState,
                        onUnlockPremiumClick = {
                            showPaywall = true
                        },
                        onViewAllClick = { categoryId ,categoryName->
                            chromeState.navigateTo(ViewAll(categoryId,categoryName))
                        },
                        onPromptClick = { promptId, categoryId ->
                            chromeState.navigateTo(
                                PromptDetail(
                                    promptId = promptId,
                                    categoryId = categoryId
                                )
                            )
                        }
                    )
                }

                is ViewAll -> NavEntry(key) {
                    ViewAllScreen(
                        chromeState = chromeState,
                        categoryId = key.categoryId,
                        onBackClick = {
                            goBack()
                        },
                        onPromptClick = { promptId ->
                            chromeState.navigateTo(PromptDetail(promptId,categoryId = key.categoryId))
                        },
                        categoryName = key.categoryName
                    )
                }

                is Search -> NavEntry(key) {
                    SearchScreen(
                        chromeState = chromeState,
                        onPromptClick = { promptId ->
                            chromeState.navigateTo(PromptDetail(promptId))
                        },
                        onBackClick = {
                            goBack()
                        }
                    )
                }


                Profile -> NavEntry(key) {
                    ProfileScreen(
                        chromeState =chromeState,
                        onLogout = onLogout,
                        onUpgradeClick = {
                            showPaywall = true
                        },
                        onRequestNotificationPermission = onRequestNotificationPermission,
                    )
                }

                is Favourite -> NavEntry(key) {
                    FavouriteScreen(
                        chromeState = chromeState,
                        onBackClick = {
                            goBack()
                        },
                        onPromptClick = { promptId ->
                            chromeState.navigateTo(PromptDetail(promptId))
                        },
                    )
                }

                is PromptDetail -> NavEntry(key) {
                    PromptDetailScreen(
                       // chromeState = chromeState,
                        promptId = key.promptId,
                        onBackClick = {
                            goBack()

                        },
                        onSubScribeClick = {
                            showPaywall = true
                        }
                    )
                }

                else -> error("Unknown key: $key")
            }
        }

        val current = backStack.lastOrNull()
        if (current in bottomRoutes && !showPaywall ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)

            ) {
                ScrollAwareBottomBar(visible = chromeState.bottomBarVisible) {
                    when (navigationStyle) {
                        NavigationStyle.MATERIAL -> {
                            BottomNavBar(backStack)
                        }

                        NavigationStyle.FLOATING -> {
                            BottomNavBarFloating(backStack)
                        }
                    }
                }
            }
        }

        if (showPaywall) {

            Paywall(
                options = PaywallOptions.Builder(
                    dismissRequest = {
                        showPaywall = false
                    }
                ).apply {

                    listener = object : PaywallListener {

                        override fun onPurchaseCompleted(
                            customerInfo: CustomerInfo,
                            storeTransaction: StoreTransaction
                        ) {
                            showPaywall = false
                            onPurchaseCompleted()
                        }

                        override fun onRestoreCompleted(
                            customerInfo: CustomerInfo
                        ) {
                            showPaywall = false
                            onPurchaseCompleted()
                        }
                    }

                }.build()
            )
        }
    }
}

class ChromeState(
    val navigateTo: (NavKey) -> Unit
) {
    var bottomBarVisible by mutableStateOf(true)
}

@Composable
fun rememberChromeState(navigateTo: (NavKey) -> Unit): ChromeState {
    return remember(navigateTo) {
        ChromeState(navigateTo = navigateTo)
    }
}
