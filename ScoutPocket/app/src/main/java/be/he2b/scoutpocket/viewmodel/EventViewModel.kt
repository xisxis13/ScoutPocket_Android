package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.entity.Presence
import be.he2b.scoutpocket.database.repository.EventRepository
import be.he2b.scoutpocket.database.repository.MemberRepository
import be.he2b.scoutpocket.database.repository.PresenceRepository
import be.he2b.scoutpocket.database.repository.RoomEventRepository
import be.he2b.scoutpocket.database.repository.RoomMemberRepository
import be.he2b.scoutpocket.database.repository.RoomPresenceRepository
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.next
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

data class EventUiState(
    val event: Event? = null,
    val membersConcerned: List<Member> = emptyList(),
    val presences: List<Presence> = emptyList(),
    val totalMembersPresent: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val isEventCreated: Boolean = false,
    val nameError: Int? = null,
    val locationError: Int? = null,
)

class EventViewModel (
    private val eventRepository: EventRepository,
    private val memberRepository: MemberRepository,
    private val presenceRepository: PresenceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    // New event
    var newEventName = MutableStateFlow("")
    var newEventSection = MutableStateFlow(Section.UNITE)
    var newEventDate = MutableStateFlow(LocalDate.now())
    var newEventStartTime = MutableStateFlow(LocalTime.of(14, 0))
    var newEventEndTime = MutableStateFlow(LocalTime.of(17, 30))
    var newEventLocation = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadEvent(eventId: String) {
        if (_uiState.value.event == null) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        }

        viewModelScope.launch {
            eventRepository.getEventById(eventId)
                .filterNotNull()
                .flatMapLatest { event ->
                    val membersFlow = if (event.section == Section.UNITE) {
                        memberRepository.getAllMembers()
                    } else {
                        memberRepository.getMembersBySection(event.section)
                    }

                    val presencesFlow = presenceRepository.getPresencesByEvent(eventId)

                    combine(membersFlow, presencesFlow) { members, presences ->
                        EventUiState(
                            event = event,
                            membersConcerned = members,
                            presences = presences,
                            totalMembersPresent = presences.count { it.status == PresenceStatus.PRESENT },
                            isLoading = false,
                            errorMessage = null,
                            isEventCreated = _uiState.value.isEventCreated,
                            nameError = _uiState.value.nameError,
                            locationError = _uiState.value.locationError
                        )
                    }
                }
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(errorMessage = R.string.events_loading_error, isLoading = false) }
                }
                .collectLatest { newState ->
                    _uiState.value = newState
                }
        }
    }

    private fun validateForm(): Boolean {
        _uiState.update { it.copy(nameError = null, locationError = null) }

        var isValid = true

        if (newEventName.value.isBlank()) {
            _uiState.update { it.copy(nameError = R.string.event_name_error) }
            isValid = false
        } else if (newEventLocation.value.isBlank()) {
            _uiState.update { it.copy(nameError = R.string.event_location_error) }
            isValid = false
        }

        return isValid
    }

    fun createEvent() {
        if (!validateForm()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isEventCreated = false) }

        viewModelScope.launch {
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

                val membersForNewEvent = if (newEvent.section != Section.UNITE) {
                    memberRepository.getMembersBySection(newEvent.section).first()
                } else {
                    memberRepository.getAllMembers().first()
                }

                val presencesToInsert = membersForNewEvent.map { member ->
                    Presence(
                        eventId = newEvent.id,
                        memberId = member.id,
                        status = PresenceStatus.DEFAULT,
                    )
                }

                presenceRepository.addPresences(presencesToInsert)
                _uiState.update { it.copy(isEventCreated = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = R.string.new_event_creation_error, isLoading = false) }
            }
        }
    }

    fun updatePresenceStatus(eventId: String, memberId: String) {
        viewModelScope.launch {
            try {
                val currentPresence = _uiState.value.presences.find {
                    it.eventId == eventId && it.memberId == memberId
                }

                if (currentPresence != null) {
                    val updatedPresence = currentPresence.copy(
                        status = currentPresence.status.next()
                    )
                    presenceRepository.updatePresence(updatedPresence)
                } else {
                    val newPresence = Presence(
                        eventId = eventId,
                        memberId = memberId,
                        status = PresenceStatus.PRESENT
                    )
                    presenceRepository.addPresences(listOf(newPresence))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = R.string.presence_update_error) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetEventCreationState() {
        _uiState.update { it.copy(isEventCreated = false, nameError = null, locationError = null) }
        resetForm()
    }

    private fun resetForm() {
        newEventName.value = ""
        newEventSection.value = Section.UNITE
        newEventDate.value = LocalDate.now()
        newEventStartTime.value = LocalTime.of(14, 0)
        newEventEndTime.value = LocalTime.of(17, 30)
        newEventLocation.value = ""
    }

}

class EventViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            val eventRepository = RoomEventRepository(context)
            val memberRepository = RoomMemberRepository(context)
            val presenceRepository = RoomPresenceRepository(context)
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventRepository, memberRepository, presenceRepository) as T
        }
        throw IllegalArgumentException(context.getString(R.string.unknown_viewmodel_class, modelClass.name))
    }
}
