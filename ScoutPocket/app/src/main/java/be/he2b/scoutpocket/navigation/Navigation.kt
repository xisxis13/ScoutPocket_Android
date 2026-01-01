package be.he2b.scoutpocket.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.CalendarFold
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users

enum class AppScreen(val route: String) {
    Login("login"),
    Main("main"),
    Home("home"),
    Agenda("agenda"),
    Members("members"),
    AddEvent("add_event"),
    AddMember("add_member?mode={mode}"),
    EventDetails("eventDetails/{eventId}"),
    Profile("profile"),
    About("about");

    companion object {
        fun addMemberRoute(mode: String = "manual") = "add_member?mode=$mode"
        fun eventDetailsRoute(eventId: Int) = "eventDetails/$eventId"
    }
}

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Home(
        route = AppScreen.Home.route,
        label = "Accueil",
        icon = Lucide.House,
    ),
    Agenda(
        route = AppScreen.Agenda.route,
        label = "Agenda",
        icon = Lucide.CalendarFold,
    ),
    Members(
        route = AppScreen.Members.route,
        label = "Membres",
        icon = Lucide.Users,
    ),
    Profile(
        route = AppScreen.Profile.route,
        label = "Profil",
        icon = Lucide.User,
    ),
}