package ai.achaialabs.helios.heliosApp.app

import ai.achaialabs.helios.heliosApp.ad.AdManager
import ai.achaialabs.helios.heliosApp.data.local.AppPreference
import ai.achaialabs.helios.heliosApp.data.local.NavigationStyle
import ai.achaialabs.helios.heliosApp.data.remote.service.SubscriptionManager
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.IsLoggedInUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.LogoutUseCase
import ai.achaialabs.helios.heliosApp.firebase.analytics.AnalyticsService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val supabase: SupabaseClient,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val subscriptionManager: SubscriptionManager,
    private val appPreference: AppPreference,
    private val adManager: AdManager,
    private val analytics: AnalyticsService,
) : ViewModel() {

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState = _appState.asStateFlow()

    private val _shouldShowNotificationPrompt =
        MutableStateFlow(false)

    val shouldShowNotificationPrompt =
        _shouldShowNotificationPrompt.asStateFlow()

    val isDarkTheme = appPreference.themeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val navigationStyle = appPreference.navigationStyleFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NavigationStyle.MATERIAL
    )



    init {
        preloadAds()
        checkSession()
    }


    private fun preloadAds() {

        adManager.preloadRewardedAd()

        adManager.preloadNativeAd()
    }


    private fun checkSession() {
        viewModelScope.launch {


            _appState.value = AppState.Loading

            supabase.auth.awaitInitialization()
            val user = supabase.auth.currentUserOrNull()


            if (user == null) {


                _appState.value = AppState.Unauthenticated
                return@launch
            }


            _appState.value = AppState.Authenticated


            try {

                Purchases.sharedInstance.logIn(
                    newAppUserID = user.id,
                    onSuccess = { _, _ ->

                        viewModelScope.launch {
                            subscriptionManager.syncSubscriptionStatus(force = false)
                        }
                    },
                    onError = { error ->
                        println("RC ERROR = ${error.message}")
                    }
                )

            } catch (e: Exception) {

                println("RC EXCEPTION = ${e.message}")
            }
        }
    }

    // Call this when LoginScreen finishes successfully
    fun onLoginSuccess() {

        viewModelScope.launch {

            val user =
                supabase.auth.currentUserOrNull()

            if (user != null) {

                Purchases.sharedInstance.logIn(
                    newAppUserID = user.id,
                    onSuccess = { _, _ ->

                        viewModelScope.launch {

                            viewModelScope.launch {
                                subscriptionManager.syncSubscriptionStatus(force = false)
                            }

                            _appState.value =
                                AppState.Authenticated
                        }
                    },
                    onError = { error ->

                        println(
                            "RC Login Error: ${error.message}"
                        )

                        _appState.value =
                            AppState.Authenticated
                    }
                )
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
            Purchases.sharedInstance.logOut(
                onError = { error ->
                   // println("RC Logout Error: ${error.message}")
                    // You can still proceed to unauthenticated state even if RC fails
                    _appState.value = AppState.Unauthenticated
                },
                onSuccess = {
                    println("RC Logged out successfully")
                    _appState.value = AppState.Unauthenticated
                }
            )
            _appState.value = AppState.Unauthenticated
        }
    }

    fun refreshSubscription() {
        viewModelScope.launch {
            subscriptionManager.syncSubscriptionStatus(force = true)
        }
    }



    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch {

            if (appPreference.themeFlow.first() == isDark) return@launch

            appPreference.saveTheme(isDark)

            analytics.logEvent(
                "theme_changed",
                mapOf(
                    "theme" to if (isDark) "dark" else "light"
                )
            )
        }
    }

    fun checkNotificationPrompt(
        permissionGranted: Boolean
    ) {
        viewModelScope.launch {

            _shouldShowNotificationPrompt.value = false

            val lastPromptTime =
                appPreference
                    .lastNotificationPromptTimeFlow
                    .first()

            _shouldShowNotificationPrompt.value =
                appPreference.shouldShowPrompt(
                    lastPromptTime = lastPromptTime,
                    permissionGranted = permissionGranted
                )
        }
    }
    fun postponeNotificationPrompt() {
        viewModelScope.launch {
            appPreference.saveLastNotificationPromptTime()
            hideNotificationPrompt()
        }
    }


    fun hideNotificationPrompt() {
        _shouldShowNotificationPrompt.value = false
    }

    fun setNavigationStyle(style: NavigationStyle) {
        viewModelScope.launch {

            val currentStyle = navigationStyle.value

            if (currentStyle == style) return@launch

            appPreference.saveNavigationStyle(style)

            analytics.logEvent(
                name = "navigation_style_changed",
                params = mapOf(
                    "navigation_style" to style.name.lowercase(),
                    "previous_style" to currentStyle.name.lowercase()
                )
            )
        }
    }
}