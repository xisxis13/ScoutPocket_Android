package be.he2b.scoutpocket.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
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
import java.io.BufferedReader
import java.io.InputStreamReader

class MemberViewModel(
    private val memberRepository: MemberRepository,
    private val eventRepository: EventRepository,
    private val presenceRepository: PresenceRepository,
) : ViewModel() {

    var csvFileContent = mutableStateOf<String?>(null)
        private set

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
    var importSuccessMessage = mutableStateOf<String?>(null)
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

    fun importMembers(context: Context, fileUri: Uri) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            csvFileContent.value = null

            try {
                val fileName = getFileName(context, fileUri)
                if (!fileName.endsWith(".csv", ignoreCase = true)) {
                    errorMessage.value = "Le fichier doit avoir l\'extension .csv"
                    return@launch
                }

                val mimeType = context.contentResolver.getType(fileUri)
                val validMimeTypes = listOf(
                    "text/csv",
                    "text/comma-separated-values",
                    "text/plain"
                )
                if (mimeType !in validMimeTypes) {
                    errorMessage.value = "Type de fichier non supporté: $mimeType"
                    return@launch
                }

                val contentResolver = context.contentResolver
                contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = reader.readText()

                    if (!isValidCSV(content)) {
                        errorMessage.value = "Le fichier n\'est pas un CSV valide"
                        return@launch
                    }

                    val importedMembers = parseCSV(content)
                    if (importedMembers.isEmpty()) {
                        errorMessage.value = "Aucun membre valide trouvé dans le fichier"
                        return@launch
                    }

                    val existingMembers = memberRepository.getAllMembers()

                    val (duplicates, newMembers) = importedMembers.partition { newMember ->
                        existingMembers.any { existing ->
                            existing.lastName.lowercase() == newMember.lastName.lowercase() &&
                                    existing.firstName.lowercase() == newMember.firstName.lowercase()
                        }
                    }

                    if (duplicates.isNotEmpty()) {
                        duplicates.forEach { duplicates ->
                            Log.w("CSV", "Membre déjà existant ignoré: ${duplicates.firstName} ${duplicates.lastName}")
                        }
                    }

                    if (newMembers.isEmpty()) {
                        errorMessage.value = "Tous les membres du fichier existent déjà dans la base"
                        return@launch
                    }

                    newMembers.forEach { member ->
                        val memberId = memberRepository.addMember(member)

                        val allEvents = eventRepository.getAllEvents().first()
                        val relevantEvents = allEvents.filter { event ->
                            event.section == member.section || event.section == Section.UNITE
                        }

                        val presencesToInsert = relevantEvents.map { event ->
                            Presence(
                                eventId = event.id,
                                memberId = memberId.toInt(),
                                status = PresenceStatus.DEFAULT,
                            )
                        }

                        if (presencesToInsert.isNotEmpty()) {
                            presenceRepository.addPresences(presencesToInsert)
                        }
                    }

                    csvFileContent.value = content
                    loadMembers()

                    val message = buildString {
                        append("✓ ${newMembers.size} membre(s) importé(s)")
                        if (duplicates.isNotEmpty()) {
                            append("\n⚠ ${duplicates.size} doublon(s) ignoré(s)")
                        }
                    }
                    Log.i("CSV", message)
                    importSuccessMessage.value = message
                }
            } catch (e: Exception) {
                Log.e("CSV", "Erreur lors de la lecture", e)
                errorMessage.value = "Erreur lors de la lecture du fichier."
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        return fileName
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
