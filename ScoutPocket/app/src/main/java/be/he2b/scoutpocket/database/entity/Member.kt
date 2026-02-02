package be.he2b.scoutpocket.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import be.he2b.scoutpocket.model.Section
import java.util.UUID

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val lastName: String,
    val firstName: String,
    val section: Section,
)
