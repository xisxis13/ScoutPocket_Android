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

    // New event
    var newEventName = mutableStateOf("")
    var newEventSection = mutableStateOf(Section.UNITE)
    var newEventDate = mutableStateOf(LocalDate.now())
    var newEventStartTime = mutableStateOf(LocalTime.of(14, 0))
    var newEventEndTime = mutableStateOf(LocalTime.of(17, 30))
    var newEventLocation = mutableStateOf("")
    var newEventIsCreated = mutableStateOf(false)

    var showUpcomingEvents = mutableStateOf(true)
    var upcomingEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var pastEvents = mutableStateOf<List<Event>>(emptyList())
        private set
    var allEvents = mutableStateOf<List<Event>>(emptyList())
        private set

    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var newEventNameError = mutableStateOf<String?>(null)
    var newEventLocationError = mutableStateOf<String?>(null)

    init {
        loadEvents()
    }

    private fun validateForm(): Boolean {
        newEventNameError.value = null
        newEventLocationError.value = null

        var isValid = true

        if (newEventName.value.isBlank()) {
            newEventNameError.value = "Le nom ne peut pas être vide"
            isValid = false
        } else if (newEventLocation.value.isBlank()) {
            newEventLocationError.value = "Le lieu ne peut pas être vide"
            isValid = false
        }

        return isValid
    }

    fun loadEvents() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                eventRepository.getAllEvents().collect { list ->
                    val today = LocalDate.now()
                    val (future, past) = list.partition {
                        it.date.isAfter(today) || it.date.isEqual(today)
                    }

                    upcomingEvents.value = future
                    pastEvents.value = past.reversed()

                    allEvents.value = list
                    isLoading.value = false
                }
            } catch (e: Exception) {
                errorMessage.value = R.string.events_loading_error.toString()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addEvent() {
        if (!validateForm()) {
            return
        }

        errorMessage.value = null
        newEventIsCreated.value = false

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val newEvent = Event(
                    name = newEventName.value.trim(),
                    section = newEventSection.value,
                    date = newEventDate.value,
                    startTime = newEventStartTime.value,
                    endTime = newEventEndTime.value,
                    location = newEventLocation.value.trim()
                )
                eventRepository.addEvent(newEvent)
                newEventIsCreated.value = true
            } catch (e: Exception) {
                errorMessage.value = R.string.new_event_creation_error.toString()
            } finally {
                isLoading.value = false
                newEventName.value = ""
                newEventLocation.value = ""
            }
        }
    }

    fun resetEventCreationState() {
        newEventIsCreated.value = false
    }

    fun switchEventsView() {
        showUpcomingEvents.value = !showUpcomingEvents.value
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