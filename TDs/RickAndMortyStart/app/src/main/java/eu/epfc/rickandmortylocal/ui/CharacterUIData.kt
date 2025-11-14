package eu.epfc.rickandmortylocal.ui

import androidx.compose.ui.graphics.Color

data class CharacterUIData(
    val name : String,
    val status : String,
    val species : String,
    val gender : String,
    val imageResource : Int,
    val backgroundColor : Color
)