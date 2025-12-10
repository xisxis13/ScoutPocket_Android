package be.he2b.scoutpocket.model

import androidx.compose.ui.graphics.Color
import be.he2b.scoutpocket.ui.theme.TagBaladinsBackground
import be.he2b.scoutpocket.ui.theme.TagBaladinsText
import be.he2b.scoutpocket.ui.theme.TagEclaireursBackground
import be.he2b.scoutpocket.ui.theme.TagEclaireursText
import be.he2b.scoutpocket.ui.theme.TagLouveteauxBackground
import be.he2b.scoutpocket.ui.theme.TagLouveteauxText
import be.he2b.scoutpocket.ui.theme.TagPionniersBackground
import be.he2b.scoutpocket.ui.theme.TagPionniersText
import be.he2b.scoutpocket.ui.theme.TagUniteBackground
import be.he2b.scoutpocket.ui.theme.TagUniteText

enum class Section(val label: String) {
    UNITE("Unité"),
    BALADINS("Baladins"),
    LOUVETEAUX("Louveteaux"),
    ECLAIREURS("Éclaireurs"),
    PIONNIERS("Pionniers"),
}

fun Section.backgroundColor(): Color = when (this) {
    Section.UNITE -> TagUniteBackground
    Section.BALADINS -> TagBaladinsBackground
    Section.LOUVETEAUX -> TagLouveteauxBackground
    Section.ECLAIREURS -> TagEclaireursBackground
    Section.PIONNIERS -> TagPionniersBackground
}

fun Section.textColor(): Color = when (this) {
    Section.UNITE -> TagUniteText
    Section.BALADINS -> TagBaladinsText
    Section.LOUVETEAUX -> TagLouveteauxText
    Section.ECLAIREURS -> TagEclaireursText
    Section.PIONNIERS -> TagPionniersText
}