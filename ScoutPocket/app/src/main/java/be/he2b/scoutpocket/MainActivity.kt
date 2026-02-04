package be.he2b.scoutpocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.network.SupabaseClient
import be.he2b.scoutpocket.ui.screens.LoginScreen
import be.he2b.scoutpocket.ui.screens.MainScreen
import be.he2b.scoutpocket.ui.screens.UnitSetupScreen
import be.he2b.scoutpocket.ui.screens.WaitingRoomScreen
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import be.he2b.scoutpocket.viewmodel.LoginViewModelFactory
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseClient.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            ScoutPocketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val loginViewModel: LoginViewModel = viewModel(
                        factory = LoginViewModelFactory(LocalContext.current.applicationContext)
                    )
                    val uiState by loginViewModel.uiState.collectAsState()
                    val navController = rememberNavController()

                    val isAuthenticated by remember(loginViewModel) {
                        loginViewModel.uiState
                            .map { it.isAuthenticated }
                            .distinctUntilChanged()
                    }.collectAsState(initial = false)

                    val isPendingApproval by remember(loginViewModel) {
                        loginViewModel.uiState
                            .map { it.isPendingApproval }
                            .distinctUntilChanged()
                    }.collectAsState(initial = false)

                    val needsUnitSetup by remember(loginViewModel) {
                        loginViewModel.uiState
                            .map { it.needsUnitSetup }
                            .distinctUntilChanged()
                    }.collectAsState(initial = false)

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    LaunchedEffect(isAuthenticated, isPendingApproval, needsUnitSetup, currentRoute) {
                        when {
                            isAuthenticated -> {
                                if (currentRoute == AppScreen.Login.route ||
                                    currentRoute == AppScreen.UnitSetup.route ||
                                    currentRoute == AppScreen.WaitingRoom.route) {

                                    navController.navigate(AppScreen.Main.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                            isPendingApproval -> {
                                if (currentRoute != AppScreen.WaitingRoom.route) {
                                    navController.navigate(AppScreen.WaitingRoom.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                            needsUnitSetup -> {
                                if (currentRoute != AppScreen.UnitSetup.route) {
                                    navController.navigate(AppScreen.UnitSetup.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                            else -> {
                                if (currentRoute != AppScreen.Login.route) {
                                    navController.navigate(AppScreen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    NavHost(
                        navController,
                        startDestination = AppScreen.Login.route,
                    ) {
                        composable(AppScreen.Login.route) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                navController = navController,
                            )
                        }
                        composable(AppScreen.UnitSetup.route) {
                            UnitSetupScreen(
                                navController = navController,
                                onCancel = {
                                    loginViewModel.cancelRegistration()
                                },
                                onSuccess = {
                                    loginViewModel.refreshUserStatus()
                                },
                            )
                        }
                        composable(AppScreen.WaitingRoom.route) {
                            WaitingRoomScreen(
                                onRefresh = {
                                    loginViewModel.refreshUserStatus()
                                },
                                onLogout = {
                                    loginViewModel.logout()
                                    navController.navigate(AppScreen.Login.route) { popUpTo(0) }
                                }
                            )
                        }
                        composable(AppScreen.Main.route) {
                            MainScreen(
                                onLogout = {
                                    loginViewModel.logout()
                                    navController.navigate(AppScreen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
