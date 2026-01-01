package be.he2b.scoutpocket.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// M3 Expressive - Formes variées pour hiérarchie visuelle
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),      // Petits chips, badges
    small = RoundedCornerShape(12.dp),          // Boutons, petites cartes
    medium = RoundedCornerShape(16.dp),         // Cartes moyennes, dialogs
    large = RoundedCornerShape(24.dp),          // Grandes cartes, bottom sheets
    extraLarge = RoundedCornerShape(32.dp),     // Hero sections, images
)
