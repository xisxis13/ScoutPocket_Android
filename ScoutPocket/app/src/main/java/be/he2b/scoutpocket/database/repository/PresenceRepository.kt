package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Presence
import kotlinx.coroutines.flow.Flow

class PresenceRepository(context: Context) {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val presenceDao = db.presenceDao()

    suspend fun addPresences(presences: List<Presence>) {
        presenceDao.insertAll(presences)
    }

    suspend fun updatePresence(presence: Presence) {
        presenceDao.update(presence)
    }

    fun getPresencesByEvent(eventId: String): Flow<List<Presence>> {
        return presenceDao.getPresencesByEvent(eventId)
    }

}