package labo.roomdemo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(item: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    @Delete
    suspend fun deleteNote(item: NoteItem)

    @Query("SELECT * FROM NoteItems")
    suspend fun getAllNotes(): List<NoteItem>
}