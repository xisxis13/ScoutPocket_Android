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
) : ViewModel() {

    var email = mutableStateOf("")
    var isEmailValid = mutableStateOf(true)

    var password = mutableStateOf("")
    var isPasswordValid = mutableStateOf(true)
    var isAuthenticated = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

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

    fun checkEmail(email: String): Boolean {
        val isValid = emailRegex.matches(email)
        isEmailValid.value = isValid

        if (!isValid) {
            errorMessage.value = "Email non valide"
        }

        return isValid
    }

    fun authenticate(email: String, password: String) {
        errorMessage.value = null
        isAuthenticated.value = false

        if (!checkEmail(email)) {
            return
        }

        isLoading.value = true

        viewModelScope.launch {
            try {
                val authBody = AuthBody(email, password)
                val response = AuthService.authClient.postAuth(authBody)

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        authManager.saveAuthData(
                            accessToken = authResponse.accessToken,
                            refreshToken = authResponse.refreshToken,
                            expiresAt = authResponse.expiresAt,
                            email = email,
                        )
                        isAuthenticated.value = true
                    }
                } else {
                    errorMessage.value = R.string.wrong_login_or_password_error.toString()
                }
            } catch (e: Exception) {
                errorMessage.value = R.string.auth_connection_error.toString()
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
    }

}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(AuthManager(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
