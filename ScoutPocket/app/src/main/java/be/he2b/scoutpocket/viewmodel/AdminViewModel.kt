package be.he2b.scoutpocket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.database.entity.UnitMembership
import be.he2b.scoutpocket.network.SupabaseClient
import be.he2b.scoutpocket.utils.SessionManager
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val pendingRequests: List<UnitMembership> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null,
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadPendingRequests()
    }

    fun loadPendingRequests() {
        val unitId = SessionManager.currentUnitId.value
        Log.d("AdminViewModel", "Chargement pour Unit ID: $unitId")

        if (unitId == null) {
            Log.d("AdminViewModel", "ERREUR: UnitID est null")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val requests = SupabaseClient.client.from("unit_memberships")
                    .select {
                        filter {
                            eq("unit_id", unitId)
                            eq("status", "PENDING")
                        }
                    }
                    .decodeList<UnitMembership>()

                Log.d("AdminViewModel", "Résultat: ${requests.size} demandes trouvées.")
                _uiState.update { it.copy(pendingRequests = requests, isLoading = false) }
            } catch (e: Exception) {
                Log.d("AdminViewModel", "Crash Supabase: ${e.message}")
                _uiState.update { it.copy(isLoading = false, message = "Erreur de chargement") }
            }
        }
    }

    fun approveUser(userId: String) {
        val unitId = SessionManager.currentUnitId.value ?: return
        updateStatus(userId, unitId, "APPROVED")
    }

    fun rejectUser(userId: String) {
        val unitId = SessionManager.currentUnitId.value ?: return

        viewModelScope.launch {
            try {
                SupabaseClient.client.from("unit_memberships")
                    .delete {
                        filter {
                            eq("unit_id", unitId)
                            eq("user_id", userId)
                        }
                    }

                loadPendingRequests()
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Erreur lors du rejet") }
            }
        }
    }

    private fun updateStatus(userId: String, unitId: String, status: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("unit_memberships").update(
                    { set("status", status) }
                ) {
                    filter {
                        eq("unit_id", unitId)
                        eq("user_id", userId)
                    }
                }
                loadPendingRequests()
                _uiState.update { it.copy(message = "Utilisateur accepté !") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "Erreur mise à jour") }
            }
        }
    }

}

class AdminViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AdminViewModel() as T
    }
}