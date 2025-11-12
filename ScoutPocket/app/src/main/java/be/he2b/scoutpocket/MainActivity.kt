package be.he2b.scoutpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.he2b.scoutpocket.ui.LoginScreen
import be.he2b.scoutpocket.ui.MainScreen
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme
import be.he2b.scoutpocket.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoutPocketTheme {
                val loginViewModel: LoginViewModel = viewModel()
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = Screens.Login.name
                ) {
                    composable(Screens.Login.name) {
                        LoginScreen(
                            viewModel = loginViewModel,
                            navController = navController,
                        )
                    }
                    composable(Screens.Main.name) {
                        MainScreen()
                    }
                }
            }
        }
    }
}
