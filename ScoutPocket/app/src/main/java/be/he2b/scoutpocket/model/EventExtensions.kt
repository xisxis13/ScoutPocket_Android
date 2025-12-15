package be.he2b.scoutpocket.model

import be.he2b.scoutpocket.database.entity.Event
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateBadgeFormatter =
    DateTimeFormatter.ofPattern("EEE d MMM", Locale.FRENCH)

private val timeFormatter =
    DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH)

private val dateFormatter =
    DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)

// Example : "DIM 21 SEP"
fun Event.formattedDateShort(): String =
    date.format(dateBadgeFormatter).uppercase()

// Example : "14:00 → 17:30"
fun Event.formattedTimeRange(): String {
    val start = startTime.format(timeFormatter)
    val end = endTime.format(timeFormatter)
    return "$start → $end"
}

// Example : "21 septembre 2025"
fun Event.formattedDateLong(): String =
    date.format(dateFormatter)