package be.he2b.scoutpocket.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import be.he2b.scoutpocket.database.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events ORDER BY date, startTime")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): Event?
}