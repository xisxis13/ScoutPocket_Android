package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel (
    private val eventRepository: EventRepository,
) : ViewModel() {

    var event = mutableStateOf<Event?>(null)
        private set
    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun loadEvent(eventId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                event.value = eventRepository.getEventById(eventId = eventId)
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de récupération de l\'évènement"
            } finally {
                isLoading.value = false
            }
        }
    }

}

class EventViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            val repository = EventRepository(context)
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
