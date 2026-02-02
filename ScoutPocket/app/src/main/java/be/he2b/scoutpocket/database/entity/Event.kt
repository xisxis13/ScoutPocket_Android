package be.he2b.scoutpocket.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import be.he2b.scoutpocket.model.Section
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = false)
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val section: Section,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val location: String,
)
