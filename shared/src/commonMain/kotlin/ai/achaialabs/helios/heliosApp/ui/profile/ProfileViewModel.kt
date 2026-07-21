package ai.achaialabs.helios.heliosApp.ui.profile

import ai.achaialabs.helios.heliosApp.domain.usecase.GetPremiumStatusUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.GetCurrentUserUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.LoginUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.LogoutUseCase
import ai.achaialabs.helios.heliosApp.domain.usecase.auth.SyncUserUseCase
import ai.achaialabs.helios.heliosApp.ui.mapper.toUi
import ai.achaialabs.helios.heliosApp.ui.model.UserUi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: UserUi? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val syncUserUseCase: SyncUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val isPremium: StateFlow<Boolean> = getPremiumStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observeCurrentUser()
    }

    /**
     * Listens to the user flow. Whenever the database/repository updates,
     * the UI state will automatically reflect the new user data.
     */
    private fun observeCurrentUser() {
        getCurrentUserUseCase()
            .onEach { domainUser ->
                _uiState.update { state ->
                    // Safely map the user if it exists, otherwise pass null
                    state.copy(user = domainUser?.toUi())
                }
            }
            .launchIn(viewModelScope)
    }


    fun getEmailSupportData(type: SupportType, subject: String, body: String): String {
        val recipient = "imgurujeet@gmail.com"
        val encodedSubject = subject.replace(" ", "%20")
        val encodedBody = body.replace(" ", "%20")
        return "mailto:$recipient?subject=$encodedSubject&body=$encodedBody"
    }

    // Add this to ProfileViewModel.kt
    fun getDeveloperInquiryUri(): String {
        val recipient = "imgurujeet@gmail.com"
        val subject = "App Development Inquiry via HeliosApp"
        val body = """
        Hi Gurujeet,

        I found your contact info in the HeliosApp about section and I'm interested in working with you on a project.

        Project Description:
        [Please provide details about your project here]

        Best regards,
    """.trimIndent()

        // Use the same encoding logic you already have
        val encodedSubject = subject.replace(" ", "%20")
        val encodedBody = body.replace(" ", "%20").replace("\n", "%0A")

        return "mailto:$recipient?subject=$encodedSubject&body=$encodedBody"
    }

    enum class SupportType { REQUEST_PROMPT, FEEDBACK }
    /**
     * Syncs fresh user data from the backend.
     */
    fun syncUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            syncUserUseCase().fold(
                onSuccess = {
                    // We don't need to manually set the user here because
                    // observeCurrentUser() will catch the database update automatically.
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Failed to sync user")
                    }
                }
            )
        }
    }

    /**
     * Logs the user out and clears the session.
     */
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                logoutUseCase()
                // If logout is successful, observeCurrentUser() will likely emit null,
                // updating the UI automatically.
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to logout")
                }
            }
        }
    }

    /**
     * Handles Google Login if triggered from the Profile screen
     * (e.g., if a guest user clicks "Sign In").
     */
    fun login(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            loginUseCase(idToken).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, error = exception.message ?: "Login failed")
                    }
                }
            )
        }
    }

    /**
     * Clears any error messages from the UI state after displaying a Toast/Snackbar.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}