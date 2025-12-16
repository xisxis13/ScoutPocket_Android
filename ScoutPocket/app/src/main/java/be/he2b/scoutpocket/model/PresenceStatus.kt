package be.he2b.scoutpocket.model

import androidx.compose.ui.graphics.Color
import be.he2b.scoutpocket.ui.theme.StateAbsentBackground
import be.he2b.scoutpocket.ui.theme.StateAbsentContent
import be.he2b.scoutpocket.ui.theme.StateDefaultBackground
import be.he2b.scoutpocket.ui.theme.StateDefaultContent
import be.he2b.scoutpocket.ui.theme.StatePresentBackground
import be.he2b.scoutpocket.ui.theme.StatePresentContent

enum class PresenceStatus {
    DEFAULT,
    PRESENT,
    ABSENT,
}

fun PresenceStatus.backgroundColor(): Color = when (this) {
    PresenceStatus.DEFAULT -> StateDefaultBackground
    PresenceStatus.PRESENT -> StatePresentBackground
    PresenceStatus.ABSENT -> StateAbsentBackground
}

fun PresenceStatus.contentColor(): Color = when (this) {
    PresenceStatus.DEFAULT -> StateDefaultContent
    PresenceStatus.PRESENT -> StatePresentContent
    PresenceStatus.ABSENT -> StateAbsentContent
}

fun PresenceStatus.next(): PresenceStatus = when (this) {
    PresenceStatus.DEFAULT -> PresenceStatus.PRESENT
    PresenceStatus.PRESENT -> PresenceStatus.ABSENT
    PresenceStatus.ABSENT -> PresenceStatus.DEFAULT
}