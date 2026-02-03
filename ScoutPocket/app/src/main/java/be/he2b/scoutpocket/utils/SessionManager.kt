package be.he2b.scoutpocket.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _currentUnitId = MutableStateFlow<String?>(null)
    val currentUnitId = _currentUnitId.asStateFlow()

    var currentUserRole: String? = null
        private set
    var currentUserFirstName: String? = null
        private set
    var currentUserLastName: String? = null
        private set

    fun setSession(unitId: String, role: String, firstName: String?, lastName: String?) {
        _currentUnitId.value = unitId
        currentUserRole = role
        currentUserFirstName = firstName
        currentUserLastName = lastName
    }

    fun clearSession() {
        _currentUnitId.value = null
        currentUserRole = null
        currentUserFirstName = null
        currentUserLastName = null
    }

    fun isAdmin(): Boolean = currentUserRole == "ADMIN"

    fun getFullName(): String {
        return if (currentUserFirstName != null) {
            "$currentUserFirstName ${currentUserLastName ?: ""}".trim()
        } else {
            "Utilisateur"
        }
    }

}