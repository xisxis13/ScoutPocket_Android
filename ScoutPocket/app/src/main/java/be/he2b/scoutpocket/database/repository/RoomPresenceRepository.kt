package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Presence
import kotlinx.coroutines.flow.Flow

class RoomPresenceRepository(context: Context): PresenceRepository {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val presenceDao = db.presenceDao()

    override suspend fun addPresences(presences: List<Presence>) {
        presenceDao.insertAll(presences)
    }

    override suspend fun updatePresence(presence: Presence) {
        presenceDao.update(presence)
    }

    override fun getPresencesByEvent(eventId: String): Flow<List<Presence>> {
        return presenceDao.getPresencesByEvent(eventId)
    }

}