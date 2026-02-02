package be.he2b.scoutpocket.database.repository

import be.he2b.scoutpocket.database.entity.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun addEvent(event: Event)
    suspend fun updateEvent(event: Event)
    suspend fun deleteEvent(event: Event)
    fun getAllEvents(): Flow<List<Event>>
    fun getEventById(eventId: String): Flow<Event?>
}