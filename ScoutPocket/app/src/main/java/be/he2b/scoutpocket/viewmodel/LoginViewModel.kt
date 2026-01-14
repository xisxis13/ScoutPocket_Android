package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.network.AuthBody
import be.he2b.scoutpocket.network.AuthManager
import be.he2b.scoutpocket.network.AuthService
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authManager: AuthManager,
    private val context: Context,
) : ViewModel() {

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    var email = mutableStateOf("")
        private set
    var password = mutableStateOf("")
        private set

    var isEmailValid = mutableStateOf(true)
        private set
    var isPasswordValid = mutableStateOf(true)
        private set

    var isAuthenticated = mutableStateOf(false)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set

    init {
        checkAuthentificationStatus()
    }

    private fun checkAuthentificationStatus() {
        if (authManager.isAuthenticated()) {
            authManager.getUserEmail()?.let { savedEmail ->
                email.value = savedEmail
                isAuthenticated.value = true
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email.value = newEmail

        if (!isEmailValid.value) {
            isEmailValid.value = true
            errorMessage.value = null
        }
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword

        if (!isPasswordValid.value) {
            isPasswordValid.value = true
            errorMessage.value = null
        }
    }

    private fun validateEmail(): Boolean {
        val emailValue = email.value.trim()

        return when {
            emailValue.isEmpty() -> {
                isEmailValid.value = false
                errorMessage.value = context.getString(R.string.email_empty_error)
                false
            }
            !emailRegex.matches(emailValue) -> {
                isEmailValid.value = false
                errorMessage.value = context.getString(R.string.email_invalid_error)
                false
            }
            else -> {
                isEmailValid.value = true
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val passwordValue = password.value.trim()

        return when {
            passwordValue.isEmpty() -> {
                isPasswordValid.value = false
                errorMessage.value = context.getString(R.string.password_empty_error)
                false
            }
            else -> {
                isPasswordValid.value = true
                true
            }
        }
    }

    private fun validateForm(): Boolean {
        val isEmailOk = validateEmail()
        val isPasswordOk = validatePassword()
        return isEmailOk && isPasswordOk
    }

    fun authenticate() {
        errorMessage.value = null
        isAuthenticated.value = false

        if (!validateForm()) {
            return
        }

        isLoading.value = true

        viewModelScope.launch {
            try {
                val authBody = AuthBody(
                    email = email.value.trim(),
                    password = password.value.trim(),
                )

                val response = AuthService.authClient.postAuth(authBody)

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        authManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            expiresAt = authResponse.expiresAt,
                            email = email.value.trim(),
                        )
                        isAuthenticated.value = true
                        password.value = ""
                    } ?: run {
                        errorMessage.value = context.getString(R.string.login_network_error)
                    }
                } else {
                    errorMessage.value = context.getString(
                        R.string.login_invalid_credentials
                    )
                }
            } catch (e: Exception) {
                errorMessage.value = context.getString(R.string.login_network_error)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun logout() {
        authManager.clearAuth()
        isAuthenticated.value = false
        email.value = ""
        password.value = ""
        errorMessage.value = null
        isEmailValid.value = true
        isPasswordValid.value = true
    }

    fun clearError() {
        errorMessage.value = null
    }

}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                authManager = AuthManager(context),
                context = context,
            ) as T
        }
        throw IllegalArgumentException(
            context.getString(R.string.unknown_viewmodel_class, modelClass.name)
        )
    }
}
