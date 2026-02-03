package be.he2b.scoutpocket.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _currentUnitId = MutableStateFlow<String?>(null)
    val currentUnitId = _currentUnitId.asStateFlow()

    var currentUserRole: String? = null
        private set

    fun setSession(unitId: String, role: String) {
        _currentUnitId.value = unitId
        currentUserRole = role
    }

    fun clearSession() {
        _currentUnitId.value = null
        currentUserRole = null
    }

    fun isAdmin(): Boolean = currentUserRole == "ADMIN"

}