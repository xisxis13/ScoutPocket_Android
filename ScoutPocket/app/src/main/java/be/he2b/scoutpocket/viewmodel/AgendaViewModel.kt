package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.repository.EventRepository
import be.he2b.scoutpocket.database.repository.RoomEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class AgendaUiState(
    val upcomingEvents: List<Event> = emptyList(),
    val pastEvents: List<Event> = emptyList(),
    val allEvents: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)

class AgendaViewModel(
    private val eventRepository: EventRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                eventRepository.getAllEvents().collect { list ->
                    val today = LocalDate.now()
                    val (future, past) = list.partition {
                        it.date.isAfter(today) || it.date.isEqual(today)
                    }

                    _uiState.update { currentState ->
                        currentState.copy(
                            upcomingEvents = future,
                            pastEvents = past.reversed(),
                            allEvents = list,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = R.string.events_loading_error, isLoading = false) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

class AgendaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            val repository = RoomEventRepository(context)
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
