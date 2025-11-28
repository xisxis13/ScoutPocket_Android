package labo.roomdemo.model

import android.content.Context
import labo.roomdemo.database.NoteDatabase

object Repository {

    private var database : NoteDatabase? = null

    fun initDatabase(context: Context) {
        if (database == null) {
            database = NoteDatabase.getInstance(context)
        }
    }

}