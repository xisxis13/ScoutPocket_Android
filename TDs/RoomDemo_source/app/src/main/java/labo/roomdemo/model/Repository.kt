package labo.roomdemo.model

import android.content.Context
import labo.roomdemo.database.Converters
import labo.roomdemo.database.NoteDatabase
import labo.roomdemo.database.NoteItem
import java.util.Date

object Repository {

    private var database : NoteDatabase? = null

    fun initDatabase(context: Context) {
        if (database == null) {
            database = NoteDatabase.getInstance(context)
        }
    }

    suspend fun insertNoteInDatabase(note : String) {

        database?.let { theDataBase ->
            val newNote = NoteItem(0, note, Date())
            theDataBase.theDAO().insertNote(newNote)
        }
    }

    suspend fun deleteNoteInDatabase(note: NoteItem) {
        database?.let { theDataBase ->
            theDataBase.theDAO().deleteNote(note)
        }
    }

    suspend fun getAllNotesFromDatabase() : List<NoteItem> {
        database?.let { theDatabase ->
            return theDatabase.theDAO().getAllNotes()
        }
        return listOf()
    }

}