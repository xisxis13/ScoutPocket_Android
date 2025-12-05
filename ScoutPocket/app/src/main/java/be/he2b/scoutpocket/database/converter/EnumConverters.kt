package be.he2b.scoutpocket.database.converter

import androidx.room.TypeConverter
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.Section

class EnumConverters {

    @TypeConverter
    fun fromSection(value: String?): Section? =
        value?.let { Section.valueOf(it) }

    @TypeConverter
    fun sectionToString(section: Section?): String? =
        section?.name

    @TypeConverter
    fun fromPresenceStatus(value: String?): PresenceStatus? =
        value?.let { PresenceStatus.valueOf(it) }

    @TypeConverter
    fun presenceStatusToString(status: PresenceStatus?): String? =
        status?.name

}