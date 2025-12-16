package be.he2b.scoutpocket.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import be.he2b.scoutpocket.database.entity.Presence

@Dao
interface PresenceDao {
    @Insert
    suspend fun insert(presence: Presence)

    @Insert
    suspend fun insertAll(presences: List<Presence>)

    @Update
    suspend fun update(presence: Presence)

    @Delete
    suspend fun delete(presence: Presence)

    @Query("SELECT * FROM presences")
    suspend fun getAllPresences(): List<Presence>

    @Query("SELECT * FROM presences WHERE eventId = :eventId")
    suspend fun getPresencesByEvent(eventId: Int): List<Presence>
}