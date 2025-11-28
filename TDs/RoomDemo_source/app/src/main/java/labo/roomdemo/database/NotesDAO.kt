package labo.roomdemo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(item: NoteItem)

    @Query("SELECT * FROM NoteItems")
    suspend fun getAllNotes(): List<NoteItem>
}