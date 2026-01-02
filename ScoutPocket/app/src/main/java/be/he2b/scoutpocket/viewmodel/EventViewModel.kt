package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
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
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.next
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel (
    private val eventRepository: EventRepository,
    private val memberRepository: MemberRepository,
    private val presenceRepository: PresenceRepository,
) : ViewModel() {

    var event = mutableStateOf<Event?>(null)
        private set
    var membersConcerned = mutableStateOf<List<Member>>(emptyList())
        private set
    var presences = mutableStateOf<List<Presence>>(emptyList())
        private set
    var totalMembersPresent = mutableStateOf(0)
        private set
    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set
    var showInfos = mutableStateOf(true)

    // New event
    var newEventName = mutableStateOf("")
    var newEventSection = mutableStateOf(Section.UNITE)
    var newEventDate = mutableStateOf(LocalDate.now())
    var newEventStartTime = mutableStateOf(LocalTime.of(14, 0))
    var newEventEndTime = mutableStateOf(LocalTime.of(17, 30))
    var newEventLocation = mutableStateOf("")
    var newEventIsCreated = mutableStateOf(false)
    var newEventNameError = mutableStateOf<String?>(null)
    var newEventLocationError = mutableStateOf<String?>(null)

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

    fun loadMembersConcerned() {
        val currentEvent = event.value

        if (currentEvent == null) {
            errorMessage.value = "Impossible de charger les membres car l\'événement est manquant."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val eventPresences = presenceRepository.getPresencesByEvent(currentEvent.id)

                val allMembers = memberRepository.getAllMembers()

                membersConcerned.value = allMembers.filter { member ->
                    eventPresences.any { presence -> presence.memberId == member.id }
                }
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de récupération des membres concernés par l\'évènement"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadPresences() {
        val currentEvent = event.value

        if (currentEvent == null) {
            errorMessage.value = "Impossible de charger les membres car l\'événement est manquant."
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val loadedPresences = presenceRepository.getPresencesByEvent(currentEvent.id)
                presences.value = loadedPresences

                totalMembersPresent.value = loadedPresences.count { it.status == PresenceStatus.PRESENT }
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de récupération des présences pour l\'évènement"
            } finally {
                isLoading.value = false
            }
        }
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

    private suspend fun getMembersForSection(section: Section): List<Member> {
        return if (section != Section.UNITE) {
            memberRepository.getMembersBySection(section)
        } else {
            memberRepository.getAllMembers()
        }
    }

    fun createEvent() {
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

                val newEventId = eventRepository.addEvent(newEvent)

                val membersForNewEvent = getMembersForSection(newEvent.section)

                val presencesToInsert = membersForNewEvent.map { member ->
                    Presence(
                        eventId = newEventId.toInt(),
                        memberId = member.id,
                        status = PresenceStatus.DEFAULT,
                    )
                }

                presenceRepository.addPresences(presencesToInsert)
                newEventIsCreated.value = true
            } catch (e: Exception) {
                errorMessage.value = R.string.new_event_creation_error.toString()
            } finally {
                isLoading.value = false

                if (newEventIsCreated.value) {
                    newEventName.value = ""
                    newEventLocation.value = ""
                }
            }
        }
    }

    fun updatePresenceStatus(eventId: Int, memberId: Int) {
        viewModelScope.launch {
            try {
                val currentPresence = presences.value.find {
                    it.eventId == eventId && it.memberId == memberId
                }
                if (currentPresence != null) {
                    val updatedPresence = currentPresence.copy(
                        status = currentPresence.status.next()
                    )
                    if (updatedPresence.status == PresenceStatus.PRESENT) {
                        totalMembersPresent.value++
                    } else if (updatedPresence.status == PresenceStatus.ABSENT) {
                        totalMembersPresent.value--
                    }
                    presenceRepository.updatePresence(updatedPresence)
                    loadPresences()
                }
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de la mise à jour de la présence"
            }
        }
    }

    fun resetEventCreationState() {
        newEventIsCreated.value = false
        resetForm()
    }

    private fun resetForm() {
        newEventName.value = ""
        newEventSection.value = Section.UNITE
        newEventDate.value = LocalDate.now()
        newEventStartTime.value = LocalTime.of(14, 0)
        newEventEndTime.value = LocalTime.of(17, 30)
        newEventLocation.value = ""
        newEventNameError.value = null
        newEventLocationError.value = null
    }

    fun switchEventDetailsView() {
        showInfos.value = !showInfos.value
    }

}

class EventViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            val eventRepository = EventRepository(context)
            val memberRepository = MemberRepository(context)
            val presenceRepository = PresenceRepository(context)
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(eventRepository, memberRepository, presenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
