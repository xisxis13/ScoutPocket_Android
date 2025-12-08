package be.he2b.scoutpocket.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.network.AuthBody
import be.he2b.scoutpocket.network.AuthService
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var isEmailValid = mutableStateOf(true)

    var password = mutableStateOf("")
    var isPasswordValid = mutableStateOf(true)
    var isAuthenticated = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

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

        viewModelScope.launch {
            try {
                val authBody = AuthBody(email, password)
                val response = AuthService.authClient.postAuth(authBody)

                if (response.isSuccessful) {
                    isAuthenticated.value = true
                } else {
                    errorMessage.value = R.string.wrong_login_or_password_error.toString()
                }
            } catch (e: Exception) {
                errorMessage.value = R.string.auth_connection_error.toString()
            }
        }
    }

}