package be.he2b.scoutpocket.database.repository

import be.he2b.scoutpocket.database.entity.Presence
import kotlinx.coroutines.flow.Flow

interface PresenceRepository {
    suspend fun addPresences(presences: List<Presence>)
    suspend fun updatePresence(presence: Presence)
    fun getPresencesByEvent(eventId: String): Flow<List<Presence>>
}