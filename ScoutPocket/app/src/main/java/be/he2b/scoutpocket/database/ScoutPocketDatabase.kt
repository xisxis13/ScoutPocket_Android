package be.he2b.scoutpocket.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.he2b.scoutpocket.database.converter.EnumConverters
import be.he2b.scoutpocket.database.converter.TimeConverters
import be.he2b.scoutpocket.database.dao.EventDao
import be.he2b.scoutpocket.database.dao.MemberDao
import be.he2b.scoutpocket.database.dao.PresenceDao
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.entity.Presence

@Database(
    entities = [Event::class, Member::class, Presence::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EnumConverters::class, TimeConverters::class)
abstract class ScoutPocketDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun memberDao(): MemberDao
    abstract fun presenceDao(): PresenceDao

    companion object {
        private const val DATABASE_NAME = "scoutpocket_db"
        private var sInstance: ScoutPocketDatabase? = null

        fun getInstance(context: Context): ScoutPocketDatabase {
            if (sInstance == null) {
                val dbBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    ScoutPocketDatabase::class.java,
                    DATABASE_NAME
                )
                sInstance = dbBuilder.build()
            }
            return sInstance!!
        }
    }

}