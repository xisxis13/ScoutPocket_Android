package be.he2b.scoutpocket.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var email = mutableStateOf("")
    var isEmailValid = mutableStateOf(true)
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun checkEmail(email: String) {
        if (!emailRegex.matches(email)) {
            isEmailValid.value = false
        }
    }

}