package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Event

class EventRepository(context: Context) {

    private val db = ScoutPocketDatabase.Companion.getInstance(context)
    private val eventDao = db.eventDao()

    suspend fun addEvent(event: Event) = eventDao.insert(event)
    suspend fun getAllEvents(): List<Event> = eventDao.getAllEvents()
    suspend fun getEventById(eventId: Int): Event? = eventDao.getEventById(eventId)

}