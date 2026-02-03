package be.he2b.scoutpocket.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.UnitMembership
import be.he2b.scoutpocket.network.SupabaseClient
import be.he2b.scoutpocket.utils.SessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isAuthenticated: Boolean = false,
    val needsUnitSetup: Boolean = false,
    val isPendingApproval: Boolean = false,
    val errorMessage: Int? = null,
    val isLoading: Boolean = false,
    val isLoginMode: Boolean = true,
)

class LoginViewModel() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    init {
        checkAutoLogin()
    }

    fun toggleMode() {
        _uiState.update { it.copy(isLoginMode = !it.isLoginMode, errorMessage = null) }
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            val session = SupabaseClient.client.auth.currentSessionOrNull()

            if (session != null) {
                _uiState.update { it.copy(isLoading = true) }
                fetchUserUnit(session.user?.id ?: "")
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

        if (isValid && passwordValue.length < 6) {
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
                if (_uiState.value.isLoginMode) {
                    SupabaseClient.client.auth.signInWith(Email) {
                        this.email = this@LoginViewModel.email.value.trim()
                        this.password = this@LoginViewModel.password.value.trim()
                    }
                } else {
                    SupabaseClient.client.auth.signUpWith(Email) {
                        this.email = this@LoginViewModel.email.value.trim()
                        this.password = this@LoginViewModel.password.value.trim()
                    }
                }

                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id

                if (userId != null) {
                    fetchUserUnit(userId)
                } else {
                    throw Exception("User ID null after login")
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "Auth Error: ${e.message}")

                val errorMsg = if (_uiState.value.isLoginMode) {
                    R.string.login_invalid_credentials
                } else {
                    R.string.login_network_error
                }

                _uiState.update { it.copy(errorMessage = errorMsg, isLoading = false) }
            }
        }
    }

    private suspend fun fetchUserUnit(userId: String) {
        try {
            val memberShips = SupabaseClient.client
                .from("unit_memberships")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UnitMembership>()

            val activeMembership = memberShips.find { it.status == "APPROVED" }

            val pendingMembership = memberShips.find { it.status == "PENDING" }

            when {
                activeMembership != null -> {
                    SessionManager.setSession(
                        unitId = activeMembership.unitId,
                        role = activeMembership.role,
                        firstName = activeMembership.firstName,
                        lastName = activeMembership.lastName,
                    )
                    _uiState.update { it.copy(isAuthenticated = true, isLoading = false) }
                }
                pendingMembership != null -> {
                    _uiState.update { it.copy(isPendingApproval = true, isLoading = false) }
                }
                else -> {
                    _uiState.update { it.copy(needsUnitSetup = true, isLoading = false) }
                }
            }
        } catch (e: Exception) {
            Log.e("Auth", "Fetch Unit Error", e)
            // En cas d'erreur réseau, on ne force pas le setup, on affiche une erreur
            _uiState.update { it.copy(errorMessage = R.string.login_network_error, isLoading = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
                SessionManager.clearSession()
                email.value = ""
                password.value = ""
                _uiState.update { LoginUiState() }
            } catch (e: Exception) {

            }
        }
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}