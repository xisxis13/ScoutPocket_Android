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
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaViewModel(
    private val eventRepository: EventRepository,
    private val context: Context,
) : ViewModel() {

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

    init {
        loadEvents()
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

    fun clearError() {
        errorMessage.value = null
    }

}

class AgendaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgendaViewModel::class.java)) {
            val database = ScoutPocketDatabase.getInstance(context)
            val repository = EventRepository(context)
            @Suppress("UNCHECKED_CAST")
            return AgendaViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}