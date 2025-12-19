package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.entity.Presence
import be.he2b.scoutpocket.database.repository.EventRepository
import be.he2b.scoutpocket.database.repository.MemberRepository
import be.he2b.scoutpocket.database.repository.PresenceRepository
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MemberViewModel(
    private val memberRepository: MemberRepository,
    private val eventRepository: EventRepository,
    private val presenceRepository: PresenceRepository,
) : ViewModel() {

    // New member
    var newMemberLastName = mutableStateOf("")
    var newMemberFirstName = mutableStateOf("")
    var newMemberSection = mutableStateOf(Section.BALADINS)
    var newMemberIsCreated = mutableStateOf(false)
    var newMemberLastNameError = mutableStateOf<String?>(null)
    var newMemberFirstNameError = mutableStateOf<String?>(null)

    var members = mutableStateOf<List<Member>>(emptyList())
        private set

    var isLoading = mutableStateOf(true)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        loadMembers()
    }

    fun loadMembers() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                members.value = memberRepository.getAllMembers()
            } catch (e: Exception) {
                errorMessage.value = R.string.members_loading_error.toString()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loadSectionMembers(section: Section) {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                members.value = memberRepository.getMembersBySection(section)
            } catch (e: Exception) {
                errorMessage.value = R.string.members_loading_error.toString()
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun validateForm(): Boolean {
        newMemberLastNameError.value = null
        newMemberFirstNameError.value = null

        var isValid = true

        if (newMemberLastName.value.isBlank()) {
            newMemberLastNameError.value = "Le nom de famille ne peut pas être vide"
            isValid = false
        } else if (newMemberFirstName.value.isBlank()) {
            newMemberFirstNameError.value = "Le prénom ne peut pas être vide"
            isValid = false
        }

        return isValid
    }

    fun createMember() {
        if (!validateForm()) {
            return
        }

        errorMessage.value = null
        newMemberIsCreated.value = false

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val newMember = Member(
                    lastName = newMemberLastName.value.trim(),
                    firstName = newMemberFirstName.value.trim(),
                    section = newMemberSection.value,
                )

                val newMemberId = memberRepository.addMember(newMember)

                val allEvents = eventRepository.getAllEvents().first()
                val relevantEvents = allEvents.filter { event ->
                    event.section == newMember.section || event.section == Section.UNITE
                }

                val presencesToInsert = relevantEvents.map { event ->
                    Presence(
                        eventId = event.id,
                        memberId = newMemberId.toInt(),
                        status = PresenceStatus.DEFAULT,
                    )
                }

                if (presencesToInsert.isNotEmpty()) {
                    presenceRepository.addPresences(presencesToInsert)
                }

                newMemberIsCreated.value = true
            } catch (e: Exception) {
                errorMessage.value = "Erreur lors de la création d\'un nouveau membre"
            } finally {
                isLoading.value = false

                if (newMemberIsCreated.value) {
                    newMemberLastName.value = ""
                    newMemberFirstName.value = ""
                }
            }
        }
    }

    fun resetMemberCreationState() {
        newMemberIsCreated.value = false
    }

}

class MemberViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            val memberRepository = MemberRepository(context)
            val eventRepository = EventRepository(context)
            val presenceRepository = PresenceRepository(context)

            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(memberRepository, eventRepository, presenceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
