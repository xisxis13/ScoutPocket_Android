package labo.roomdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [NoteItem::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun theDAO() : NotesDAO

    companion object {
        private const val DATABASE_NAME = "notes_db"
        private var sInstance: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            if (sInstance == null) {
                val dbBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    DATABASE_NAME
                )
                sInstance = dbBuilder.build()
            }
            return sInstance!!
        }
    }

}