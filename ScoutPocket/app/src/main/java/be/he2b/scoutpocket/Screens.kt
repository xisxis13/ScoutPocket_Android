package be.he2b.scoutpocket

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens(val route: String) {

    Login("login"),
    Main("main"),

    Home("home"),
    About("about"),

}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Home : BottomNavItem(Screens.Home.route, Icons.Default.Home, "Accueil")
    data object About : BottomNavItem(Screens.About.route, Icons.Default.Info, "About")
}