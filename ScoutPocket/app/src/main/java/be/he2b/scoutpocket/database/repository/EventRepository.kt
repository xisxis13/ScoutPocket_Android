package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Event
import kotlinx.coroutines.flow.Flow

class EventRepository(context: Context) {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val eventDao = db.eventDao()

    suspend fun addEvent(event: Event) {
        eventDao.insert(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.update(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.delete(event)
    }

    fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    fun getEventById(eventId: String): Flow<Event?> {
        return eventDao.getEventById(eventId)
    }

}