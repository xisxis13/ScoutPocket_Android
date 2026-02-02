package be.he2b.scoutpocket.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import be.he2b.scoutpocket.model.PresenceStatus

@Entity(
    tableName = "presences",
    primaryKeys = ["eventId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Presence(
    val eventId: String,
    val memberId: String,
    val status: PresenceStatus,
)
