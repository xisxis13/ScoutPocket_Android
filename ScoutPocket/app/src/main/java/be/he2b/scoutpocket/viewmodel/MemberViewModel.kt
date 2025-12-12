package be.he2b.scoutpocket.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.repository.MemberRepository
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.launch

class MemberViewModel(
    private val memberRepository: MemberRepository,
) : ViewModel() {

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

}

class MemberViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            val repository = MemberRepository(context)
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}