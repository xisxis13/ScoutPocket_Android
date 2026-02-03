package be.he2b.scoutpocket.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Unit
import be.he2b.scoutpocket.database.entity.UnitMembership
import be.he2b.scoutpocket.network.SupabaseClient
import be.he2b.scoutpocket.utils.SessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UnitSetupUiState(
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val successMessage: String? = null,
    val foundUnits: List<Unit> = emptyList(),
    val isSetupComplete: Boolean = false,
)

class UnitSetupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UnitSetupUiState())
    val uiState: StateFlow<UnitSetupUiState> = _uiState.asStateFlow()

    val unitNameInput = MutableStateFlow("")
    val unitCodeInput = MutableStateFlow("")
    val firstNameInput = MutableStateFlow("")
    val lastNameInput = MutableStateFlow("")

    val searchInput = MutableStateFlow("")

    fun createUnit() {
        val name = unitNameInput.value.trim()
        val code = unitCodeInput.value.trim().uppercase()

        val fName = firstNameInput.value.trim()
        val lName = lastNameInput.value.trim()

        if (name.isBlank() || code.isBlank() || fName.isBlank() || lName.isBlank()) {
            // TODO: Change the error content
            _uiState.update { it.copy(errorMessage = R.string.event_name_error) }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: throw Exception("No user")

                val newUnit = Unit(
                    id = code,
                    name = name,
                )

                SupabaseClient.client.from("units").insert(newUnit)

                val membership = UnitMembership(
                    userId = userId,
                    unitId = code,
                    role = "ADMIN",
                    status = "APPROVED",
                    firstName = fName,
                    lastName = lName,
                )

                SupabaseClient.client.from("unit_memberships").insert(membership)

                SessionManager.setSession(
                    unitId = code,
                    role = "ADMIN",
                    firstName = fName,
                    lastName = lName
                )
                _uiState.update { it.copy(isSetupComplete = true, isLoading = false) }
            } catch (e: Exception) {
                // TODO: Change the error content
                // "Erreur: Ce code d'unité existe peut-être déjà."
                _uiState.update { it.copy(errorMessage = R.string.event_name_error, isLoading = false) }
            }
        }
    }

    fun searchUnits() {
        val query = searchInput.value.trim()

        if (query.length < 2) return

        viewModelScope.launch {
            try {
                val results = SupabaseClient.client.from("units")
                    .select(columns = Columns.list("id", "name")) {
                        filter {
                            or {
                                ilike("name", "%$query%")
                                ilike("id", "%$query%")
                            }
                        }
                    }
                    .decodeList<Unit>()

                _uiState.update { it.copy(foundUnits = results) }
            } catch (e: Exception) {

            }
        }
    }

    fun requestToJoin(unit: Unit) {
        val fName = firstNameInput.value.trim()
        val lName = lastNameInput.value.trim()

        if (fName.isBlank() && lName.isBlank()) {
            // TODO: Change error text : "Le nom et le prénom ne peuvent pas être vide"
            _uiState.update { it.copy(errorMessage = R.string.event_name_error) }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: throw Exception("No user")
                val unitId = unit.id

                val membership = UnitMembership(
                    userId = userId,
                    unitId = unitId,
                    role = "ANIMATEUR",
                    status = "PENDING",
                    firstName = fName,
                    lastName = lName,
                )

                SupabaseClient.client.from("unit_memberships").insert(membership)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Demande envoyée à ${unit.name} ! En attente de validation."
                    )
                }
            } catch (e: Exception) {
                // TODO: Change the error content
                _uiState.update { it.copy(errorMessage = R.string.event_name_error, isLoading = false) }
            }
        }
    }

}

class UnitSetupViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UnitSetupViewModel() as T
    }
}