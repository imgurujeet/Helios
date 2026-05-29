package ai.achaialabs.promptr.promptrApp.ui.onboarding

import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.LoginUseCase
import ai.achaialabs.promptr.promptrApp.domain.usecase.auth.SyncUserUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val syncUserUseCase: SyncUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = loginUseCase(idToken)
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun onNativeSignInSuccess() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = syncUserUseCase()
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error(result.exceptionOrNull()?.message ?: "Sync failed")
            }
        }
    }

    fun onError(message: String) {
        _uiState.value = LoginUiState.Error(message)
    }
}

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
