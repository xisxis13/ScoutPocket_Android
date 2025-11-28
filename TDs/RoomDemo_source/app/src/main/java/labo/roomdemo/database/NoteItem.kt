package labo.roomdemo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NoteItems")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val contentText : String,
)
