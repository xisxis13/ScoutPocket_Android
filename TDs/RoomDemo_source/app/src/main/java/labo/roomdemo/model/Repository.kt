package labo.roomdemo.model

import android.content.Context
import labo.roomdemo.database.NoteDatabase
import labo.roomdemo.database.NoteItem

object Repository {

    private var database : NoteDatabase? = null

    fun initDatabase(context: Context) {
        if (database == null) {
            database = NoteDatabase.getInstance(context)
        }
    }

    suspend fun insertNoteInDatabase(note : String) {
        database?.let { theDataBase ->
            val newNote = NoteItem(0, note)
            theDataBase.theDAO().insertNote(newNote)
        }
    }

    suspend fun getAllNotesFromDatabase() : List<NoteItem> {
        database?.let { theDatabase ->
            return theDatabase.theDAO().getAllNotes()
        }
        return listOf()
    }

}