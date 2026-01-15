package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Presence

class PresenceRepository(context: Context) {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val presenceDao = db.presenceDao()

    suspend fun updatePresence(presence: Presence) = presenceDao.update(presence)
    suspend fun addPresences(presences: List<Presence>) = presenceDao.insertAll(presences)
    suspend fun getPresencesByEvent(eventId: Int): List<Presence> = presenceDao.getPresencesByEvent(eventId)

}