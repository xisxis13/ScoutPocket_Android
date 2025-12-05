package be.he2b.scoutpocket.database.converter

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class TimeConverters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? =
        value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? =
        date?.toEpochDay()

    @TypeConverter
    fun fromSecondsOfDay(value: Int?): LocalTime? =
        value?.let { LocalTime.ofSecondOfDay(it.toLong()) }

    @TypeConverter
    fun localTimeToSecondsOfDay(time: LocalTime?): Int? =
        time?.toSecondOfDay()

}