package be.he2b.scoutpocket.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import be.he2b.scoutpocket.model.Section

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lastName: String,
    val firstName: String,
    val section: Section,
)
