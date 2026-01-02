package be.he2b.scoutpocket.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import be.he2b.scoutpocket.database.converter.EnumConverters
import be.he2b.scoutpocket.database.converter.TimeConverters
import be.he2b.scoutpocket.database.dao.EventDao
import be.he2b.scoutpocket.database.dao.MemberDao
import be.he2b.scoutpocket.database.dao.PresenceDao
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.database.entity.Presence
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

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
                sInstance = dbBuilder.addCallback(PrepopulateCallback(context)).build()
            }
            return sInstance!!
        }
    }

    private class PrepopulateCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                populateDatabaseWithMembers(database.memberDao())
                populateDatabaseWithEvents(
                    eventDao = database.eventDao(),
                    memberDao = database.memberDao(),
                    presenceDao = database.presenceDao(),
                )
            }
        }

        private suspend fun populateDatabaseWithEvents(
            eventDao: EventDao,
            memberDao: MemberDao,
            presenceDao: PresenceDao,
        ) {
            val sampleEvents = listOf(
                Event(
                    name = "Réunion Classique",
                    section = Section.LOUVETEAUX,
                    date = LocalDate.now().minusDays(2),
                    startTime = LocalTime.of(14, 0),
                    endTime = LocalTime.of(17, 30),
                    location = "Au local de l'unité"
                ),
                Event(
                    name = "Hike d'orientation",
                    section = Section.ECLAIREURS,
                    date = LocalDate.now().minusDays(18),
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(16, 0),
                    location = "Forêt de Soignes"
                ),
                Event(
                    name = "Découverte du cirque",
                    section = Section.BALADINS,
                    date = LocalDate.now().plusDays(37),
                    startTime = LocalTime.of(14, 0),
                    endTime = LocalTime.of(17, 30),
                    location = "Grand place de Ath"
                ),
                Event(
                    name = "Service au souper des anciens",
                    section = Section.PIONNIERS,
                    date = LocalDate.now().plusDays(41),
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(16, 0),
                    location = "La providence"
                ),
                Event(
                    name = "Soirée film",
                    section = Section.UNITE,
                    date = LocalDate.now().plusDays(59),
                    startTime = LocalTime.of(18, 0),
                    endTime = LocalTime.of(21, 0),
                    location = "Théâtre Jean-Claude Drouot"
                ),
            )

            sampleEvents.forEach { event ->
                val eventId = eventDao.insert(event)

                val membersForEvent = if (event.section != Section.UNITE) {
                    memberDao.getMembersBySection(event.section)
                } else {
                    memberDao.getAllMembers()
                }

                val presencesToInsert = membersForEvent.map { member ->
                    Presence(
                        eventId = eventId.toInt(),
                        memberId = member.id,
                        status = PresenceStatus.DEFAULT,
                    )
                }

                presenceDao.insertAll(presencesToInsert)
            }
        }

        private suspend fun populateDatabaseWithMembers(memberDao: MemberDao) {
            val sampleMembers = listOf(
                Member(
                    lastName = "Martin",
                    firstName = "Emma",
                    section = Section.BALADINS
                ),
                Member(
                    lastName = "Dupont",
                    firstName = "Lucas",
                    section = Section.BALADINS
                ),
                Member(
                    lastName = "Moreau",
                    firstName = "Lina",
                    section = Section.LOUVETEAUX
                ),
                Member(
                    lastName = "Laurent",
                    firstName = "Noah",
                    section = Section.LOUVETEAUX
                ),
                Member(
                    lastName = "Delacroix",
                    firstName = "Kilian",
                    section = Section.LOUVETEAUX
                ),
                Member(
                    lastName = "Bernard",
                    firstName = "Alice",
                    section = Section.ECLAIREURS
                ),
                Member(
                    lastName = "Robert",
                    firstName = "Gabriel",
                    section = Section.ECLAIREURS
                ),
                Member(
                    lastName = "Leblond",
                    firstName = "Lilas",
                    section = Section.ECLAIREURS
                ),
                Member(
                    lastName = "Lefèvre",
                    firstName = "Jade",
                    section = Section.PIONNIERS
                ),
                Member(
                    lastName = "Mercier",
                    firstName = "Louis",
                    section = Section.PIONNIERS
                ),
            )

            sampleMembers.forEach { member ->
                memberDao.insert(member)
            }
        }
    }


}