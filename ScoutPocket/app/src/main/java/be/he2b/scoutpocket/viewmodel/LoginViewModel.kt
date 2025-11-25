package be.he2b.scoutpocket.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    errorMessage.value = "Mauvais login ou mot de passe"
                }
            } catch (e: Exception) {
                errorMessage.value = "Erreur de connexion : impossible de joindre le serveur."
            }
        }
    }

}