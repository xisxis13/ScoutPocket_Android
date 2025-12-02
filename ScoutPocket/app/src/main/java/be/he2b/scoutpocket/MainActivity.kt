package be.he2b.scoutpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.ui.screens.LoginScreen
import be.he2b.scoutpocket.ui.screens.MainScreen
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme
import be.he2b.scoutpocket.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScoutPocketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val loginViewModel: LoginViewModel = viewModel()
                    val navController = rememberNavController()
                    NavHost(
                        navController,
                        startDestination = AppScreen.Login.name
                    ) {
                        composable(AppScreen.Login.name) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                navController = navController,
                            )
                        }
                        composable(AppScreen.Main.name) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}
