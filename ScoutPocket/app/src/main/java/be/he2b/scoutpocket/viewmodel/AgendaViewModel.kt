package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.repository.EventRepository
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AgendaViewModel(
    private val eventRepository: EventRepository,
) : ViewModel() {

    var events = mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading = mutableStateOf(true)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        loadEvents()
    }

    fun loadEvents() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                eventRepository.getAllEvents().collect { list ->
                    events.value = list
                    isLoading.value = false
                }
            } catch (e: Exception) {
                errorMessage.value = R.string.events_loading_error.toString()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addEvent(
        name: String,
        section: Section,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        location: String,
    ) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val newEvent = Event(
                    name = name,
                    section = section,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    location = location
                )
                eventRepository.addEvent(newEvent)
            } catch (e: Exception) {
                errorMessage.value = R.string.new_event_creation_error.toString()
            } finally {
                isLoading.value = false
            }
        }
    }

}

class AgendaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            val database = ScoutPocketDatabase.getInstance(context)
            val repository = EventRepository(context)
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}