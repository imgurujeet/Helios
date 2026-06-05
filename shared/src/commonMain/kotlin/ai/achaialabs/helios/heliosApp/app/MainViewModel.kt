package ai.achaialabs.helios.heliosApp.app

import ai.achaialabs.helios.heliosApp.data.remote.service.SubscriptionManager
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.IsLoggedInUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.LogoutUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revenuecat.purchases.kmp.Purchases
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val supabase: SupabaseClient,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {

    private val _appState = MutableStateFlow<AppState>(AppState.Loading)
    val appState = _appState.asStateFlow()

    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme = _isDarkTheme.asStateFlow()

    init { checkSession() }

    private fun checkSession() {
        viewModelScope.launch {
            if (isLoggedInUseCase()) {
                subscriptionManager.syncSubscriptionStatus()
                _appState.value = AppState.Authenticated
            } else {
                _appState.value = AppState.Unauthenticated
            }
        }
    }

    // Call this when LoginScreen finishes successfully
    fun onLoginSuccess() {
        viewModelScope.launch {
            subscriptionManager.syncSubscriptionStatus()
            _appState.value = AppState.Authenticated
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            logoutUseCase()
            Purchases.sharedInstance.logOut(
                onError = { error ->
                    println("RC Logout Error: ${error.message}")
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

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark

        // TODO: Later on, use DataStore or Multiplatform Settings here
        // to save this boolean so the app remembers their choice on restart!
    }
}