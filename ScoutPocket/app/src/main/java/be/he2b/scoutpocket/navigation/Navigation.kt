package be.he2b.scoutpocket.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.CalendarFold
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users

enum class AppScreen(val route: String) {
    Login("login"),
    Main("main"),
}

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Agenda(
        route = "agenda",
        label = "Agenda",
        icon = Lucide.CalendarFold,
    ),
    Members(
        route = "members",
        label = "Membres",
        icon = Lucide.Users,
    ),
    About(
        route = "about",
        label = "About",
        icon = Lucide.Info,
    ),
}