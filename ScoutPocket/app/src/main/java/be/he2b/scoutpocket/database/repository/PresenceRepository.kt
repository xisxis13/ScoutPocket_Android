package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Presence

class PresenceRepository(context: Context) {

    private val db = ScoutPocketDatabase.Companion.getInstance(context)
    private val presenceDao = db.presenceDao()

    suspend fun addPresence(presence: Presence) = presenceDao.insert(presence)
    suspend fun getAllPresences(): List<Presence> = presenceDao.getAllPresences()
    suspend fun getPresencesByEvent(eventId: Int): List<Presence> = presenceDao.getPresencesByEvent(eventId)

}