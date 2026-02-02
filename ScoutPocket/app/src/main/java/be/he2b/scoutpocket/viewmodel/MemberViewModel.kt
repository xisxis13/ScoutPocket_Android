package be.he2b.scoutpocket.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

data class MemberUiState(
    val members: List<Member> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: Int? = null,
    val importSuccessMessage: Int? = null,
    val importSuccessArgs: Pair<Int, Int>? = null,
    val isMemberCreated: Boolean = false,
    val lastNameError: Int? = null,
    val firstNameError: Int? = null,
    val csvFileContent: String? = null,
)

class MemberViewModel(
    private val memberRepository: MemberRepository,
    private val eventRepository: EventRepository,
    private val presenceRepository: PresenceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MemberUiState())
    val uiState: StateFlow<MemberUiState> = _uiState.asStateFlow()

    // New member
    var newMemberLastName = MutableStateFlow("")
    var newMemberFirstName = MutableStateFlow("")
    var newMemberSection = MutableStateFlow(Section.BALADINS)

    init {
        loadMembers()
    }

    fun loadMembers() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                memberRepository.getAllMembers().collect { list ->
                    _uiState.update { it.copy(members = list, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = R.string.members_loading_error, isLoading = false) }
            }
        }
    }

    private fun validateForm(): Boolean {
        _uiState.update { it.copy(lastNameError = null, firstNameError = null) }
        var isValid = true

        if (newMemberLastName.value.isBlank()) {
            _uiState.update { it.copy(lastNameError = R.string.member_lastname_error) }
            isValid = false
        } else if (newMemberFirstName.value.isBlank()) {
            _uiState.update { it.copy(firstNameError = R.string.member_firstname_error) }
            isValid = false
        }

        return isValid
    }

    fun createMember() {
        if (!validateForm()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null, isMemberCreated = false) }

        viewModelScope.launch {
            try {
                val newMember = Member(
                    lastName = newMemberLastName.value.trim(),
                    firstName = newMemberFirstName.value.trim(),
                    section = newMemberSection.value,
                )

                memberRepository.addMember(newMember)

                val allEvents = eventRepository.getAllEvents().first()
                val relevantEvents = allEvents.filter { event ->
                    event.section == newMember.section || event.section == Section.UNITE
                }

                val presencesToInsert = relevantEvents.map { event ->
                    Presence(
                        eventId = event.id,
                        memberId = newMember.id,
                        status = PresenceStatus.DEFAULT,
                    )
                }

                if (presencesToInsert.isNotEmpty()) {
                    presenceRepository.addPresences(presencesToInsert)
                }

                _uiState.update { it.copy(isMemberCreated = true, isLoading = false) }
                resetForm()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = R.string.member_creation_error, isLoading = false) }
            }
        }
    }

    fun importMembers(fileUri: Uri, contentResolver: ContentResolver) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, importSuccessMessage = null, csvFileContent = null) }

        viewModelScope.launch {
            try {
//                val fileName = getFileName(context, fileUri)
//                if (!fileName.endsWith(".csv", ignoreCase = true)) {
//                    errorMessage.value = context.getString(R.string.csv_extension_error)
//                    return@launch
//                }
//
//                val mimeType = context.contentResolver.getType(fileUri)
//                val validMimeTypes = listOf("text/csv", "text/comma-separated-values", "text/plain")
//                if (mimeType !in validMimeTypes) {
//                    errorMessage.value = context.getString(R.string.csv_mimetype_error, mimeType ?: "unknown")
//                    return@launch
//                }

                contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = reader.readText()

                    if (!isValidCSV(content)) {
                        _uiState.update { it.copy(errorMessage = R.string.csv_invalid_error, isLoading = false) }
                        return@launch
                    }

                    val importedMembers = parseCSV(content)
                    if (importedMembers.isEmpty()) {
                        _uiState.update { it.copy(errorMessage = R.string.csv_no_members_error, isLoading = false) }
                        return@launch
                    }

                    val existingMembers = memberRepository.getAllMembers().first()
                    val (duplicates, newMembers) = importedMembers.partition { newMember ->
                        existingMembers.any { existing ->
                            existing.lastName.equals(newMember.lastName, ignoreCase = true) &&
                                    existing.firstName.equals(newMember.firstName, ignoreCase = true)
                        }
                    }

                    if (newMembers.isEmpty()) {
                        _uiState.update { it.copy(errorMessage = R.string.csv_all_duplicates_error, isLoading = false) }
                        return@launch
                    }

                    newMembers.forEach { member ->
                        memberRepository.addMember(member)
                        val allEvents = eventRepository.getAllEvents().first()
                        val relevantEvents = allEvents.filter { event ->
                            event.section == member.section || event.section == Section.UNITE
                        }

                        val presencesToInsert = relevantEvents.map { event ->
                            Presence(
                                eventId = event.id,
                                memberId = member.id,
                                status = PresenceStatus.DEFAULT,
                            )
                        }

                        if (presencesToInsert.isNotEmpty()) {
                            presenceRepository.addPresences(presencesToInsert)
                        }
                    }

                    _uiState.update {
                        it.copy(
                            csvFileContent = content,
                            importSuccessMessage = R.string.csv_import_success,
                            importSuccessArgs = Pair(newMembers.size, duplicates.size),
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("CSV", "Error import", e)
                _uiState.update { it.copy(errorMessage = R.string.csv_read_error, isLoading = false) }
            }
        }
    }

    private fun isValidCSV(content: String): Boolean {
        if (content.isBlank()) return false

        val lines = content.lines().filter { it.isNotBlank() }
        if (lines.isEmpty()) return false

        val firstLine = lines[0]
        val delimiter = when {
            firstLine.count { it == ',' } > 0 -> ','
            firstLine.count { it == ';' } > 0 -> ';'
            else -> return false
        }

        return lines.all { line ->
            val columns = line.split(delimiter).map { it.trim() }

            if (columns.size != 3) {
                Log.w("CSV", "Ligne avec ${columns.size} colonnes: $line")
                return@all false
            }

            if (columns.any { it.isBlank() }) {
                Log.w("CSV", "Colonne vide dans la ligne: $line")
                return@all false
            }

            if (normalizeSectionName(columns[2]) == null) {
                Log.w("CSV", "Section invalide '${columns[2]}' dans la ligne: $line")
                return@all false
            }

            true
        }
    }

    private fun parseCSV(content: String): List<Member> {
        val lines = content.lines().filter { it.isNotBlank() }
        val delimiter = if (lines[0].contains(',')) ',' else ';'

        return lines.mapNotNull { line ->
            val columns = line.split(delimiter).map { it.trim() }

            if (columns.size == 3) {
                val lastName = normalizePersonName(columns[0])
                val firstName = normalizePersonName(columns[1])
                val section = normalizeSectionName(columns[2])

                if (section != null) {
                    Member(
                        lastName = lastName,
                        firstName = firstName,
                        section = section
                    )
                } else null
            } else null
        }
    }

    private fun normalizePersonName(name: String): String {
        return name.trim()
            .lowercase()
            .split(" ", "-")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
    }

    private fun normalizeSectionName(sectionName: String): Section? {
        val normalized = sectionName.trim().lowercase()
            .replace("é", "e")
            .replace("è", "e")
            .replace("ê", "e")
            .replace("ë", "e")

        return when (normalized) {
            "baladin", "baladins", "balandin", "balandins" -> Section.BALADINS

            "louveteau", "louveteaux", "louvteau", "louvteaux" -> Section.LOUVETEAUX

            "eclaireur", "eclaireurs", "éclaireur", "éclaireurs",
            "eclaireu", "eclaireus", "eclaireuse", "eclaireuses" -> Section.ECLAIREURS

            "pionnier", "pionniers", "pionier", "pioniers",
            "pionniere", "pionnieres" -> Section.PIONNIERS

            else -> {
                Log.w("CSV", "Section inconnue: $sectionName")
                null
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun resetMemberCreationState() {
        _uiState.update { it.copy(isMemberCreated = false) }
        resetForm()
    }

    private fun resetForm() {
        newMemberLastName.value = ""
        newMemberFirstName.value = ""
        newMemberSection.value = Section.BALADINS
        _uiState.update { it.copy(lastNameError = null, firstNameError = null) }
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
        throw IllegalArgumentException(context.getString(R.string.unknown_viewmodel_class, modelClass.name))
    }
}
