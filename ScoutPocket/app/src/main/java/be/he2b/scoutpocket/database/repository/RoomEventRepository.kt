package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Event
import kotlinx.coroutines.flow.Flow

class RoomEventRepository(context: Context): EventRepository {

    private val db = ScoutPocketDatabase.getInstance(context)
    private val eventDao = db.eventDao()

    override suspend fun addEvent(event: Event) {
        eventDao.insert(event)
    }

    override suspend fun updateEvent(event: Event) {
        eventDao.update(event)
    }

    override suspend fun deleteEvent(event: Event) {
        eventDao.delete(event)
    }

    override fun getAllEvents(): Flow<List<Event>> = eventDao.getAllEvents()

    override fun getEventById(eventId: String): Flow<Event?> {
        return eventDao.getEventById(eventId)
    }

}