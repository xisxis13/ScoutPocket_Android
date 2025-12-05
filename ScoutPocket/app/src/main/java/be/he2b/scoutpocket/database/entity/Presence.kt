package be.he2b.scoutpocket.database.entity

import androidx.room.Entity
import be.he2b.scoutpocket.model.PresenceStatus

@Entity(
    tableName = "presences",
    primaryKeys = ["eventId", "memberId"],
)
data class Presence(
    val eventId: Int,
    val memberId: Int,
    val status: PresenceStatus,
)
