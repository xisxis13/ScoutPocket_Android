package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.network.AuthBody
import be.he2b.scoutpocket.network.AuthManager
import be.he2b.scoutpocket.network.AuthService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isAuthenticated: Boolean = false,
    val errorMessage: Int? = null,
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    init {
        checkAuthentificationStatus()
    }

    private fun checkAuthentificationStatus() {
        if (authManager.isAuthenticated()) {
            authManager.getUserEmail()?.let { savedEmail ->
                email.value = savedEmail
                _uiState.update { it.copy(isAuthenticated = true) }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
        if (!_uiState.value.isEmailValid) {
            _uiState.update { it.copy(isEmailValid = true, errorMessage = null) }
        }
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
        if (!_uiState.value.isPasswordValid) {
            _uiState.update { it.copy(isPasswordValid = true, errorMessage = null) }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val emailValue = email.value.trim()
        val passwordValue = password.value.trim()

        if (emailValue.isEmpty()) {
            _uiState.update { it.copy(isEmailValid = false, errorMessage = R.string.email_empty_error) }
            isValid = false
        } else if (!emailRegex.matches(emailValue)) {
            _uiState.update { it.copy(isEmailValid = false, errorMessage = R.string.email_invalid_error) }
            isValid = false
        }

        if (isValid && passwordValue.isEmpty()) {
            _uiState.update { it.copy(isPasswordValid = false, errorMessage = R.string.password_empty_error) }
            isValid = false
        }

        return isValid
    }

    fun authenticate() {
        _uiState.update { it.copy(errorMessage = null, isAuthenticated = false) }

        if (!validateForm()) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val authBody = AuthBody(email.value.trim(), password.value.trim())
                val response = AuthService.authClient.postAuth(authBody)

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        authManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            expiresAt = authResponse.expiresAt,
                            email = email.value.trim(),
                        )
                        password.value = ""
                        _uiState.update { it.copy(isAuthenticated = true, isLoading = false) }
                    } ?: run {
                        _uiState.update { it.copy(errorMessage = R.string.login_network_error, isLoading = false) }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = R.string.login_invalid_credentials, isLoading = false) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = R.string.login_network_error, isLoading = false) }
            }
        }
    }

    fun logout() {
        authManager.clearAuth()
        email.value = ""
        password.value = ""
        _uiState.update { LoginUiState() }
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                authManager = AuthManager(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}