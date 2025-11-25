package be.he2b.scoutpocket

import android.os.SystemClock
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.he2b.scoutpocket.ui.screens.LoginScreen
import be.he2b.scoutpocket.ui.screens.MainScreen
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Test
    fun invalidEmail_ShowsError() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            val loginViewModel = LoginViewModel()

            LoginScreen(
                viewModel = loginViewModel,
                navController = navController,
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@invalide")
        composeTestRule.onNode(hasText("Connexion") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()
    }

    @Test
    fun badCredentials_ShowsAuthError() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            val loginViewModel = LoginViewModel()

            NavHost(
                navController,
                startDestination = Screens.Login.route
            ) {
                composable(Screens.Login.route) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController
                    )
                }
                composable(Screens.Main.route) {
                    MainScreen()
                }
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("mauvais!!")

        composeTestRule.onNode(hasText("Connexion") and hasClickAction()).performClick()

        SystemClock.sleep(3000)

        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertIsDisplayed()

        composeTestRule.onNodeWithText("ESI logo").assertDoesNotExist()
    }

    @Test
    fun correctCredentials_NavigatesToMainScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            val loginViewModel = LoginViewModel()

            NavHost(
                navController,
                startDestination = Screens.Login.route
            ) {
                composable(Screens.Login.route) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController
                    )
                }
                composable(Screens.Main.route) {
                    MainScreen()
                }
            }
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("dev5!!")

        composeTestRule.onNode(hasText("Connexion") and hasClickAction()).performClick()

        SystemClock.sleep(3000)

        composeTestRule.onNodeWithContentDescription("ESI logo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertDoesNotExist()
    }
}