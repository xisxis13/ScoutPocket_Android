package labo.roomdemo.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labo.roomdemo.database.NoteItem
import labo.roomdemo.model.Repository

class MainScreenViewModel : ViewModel() {

    val noteList : MutableState<List<NoteItem>> = mutableStateOf(listOf())

    init {
        viewModelScope.launch {
            noteList.value = Repository.getAllNotesFromDatabase()
        }
    }

    fun addNoteInTheDatabase(note: String) {
        viewModelScope.launch {
            Repository.insertNoteInDatabase(note)
            noteList.value = Repository.getAllNotesFromDatabase()
        }
    }

    fun deleteNoteInTheDatabase(note: NoteItem) {
        viewModelScope.launch {
            Repository.deleteNoteInDatabase(note)
            noteList.value = Repository.getAllNotesFromDatabase()
        }
    }

}