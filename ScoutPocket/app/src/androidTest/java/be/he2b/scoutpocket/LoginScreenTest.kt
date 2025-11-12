package be.he2b.scoutpocket

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.he2b.scoutpocket.ui.LoginScreen
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
    fun invalidMailShowsError() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            LoginScreen(
                viewModel = LoginViewModel(),
                navController = navController
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("testexample.com")
        composeTestRule.onNode(hasText("Connexion") and hasClickAction()).performClick()
        composeTestRule.onNodeWithText("invalid email").assertIsDisplayed()
    }

    @Test
    fun validMailNavigateToMainScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            LoginScreen(
                viewModel = LoginViewModel(),
                navController = navController
            )
        }

        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNode(hasText("Connexion") and hasClickAction()).performClick()
        composeTestRule.onNodeWithText("ESI logo").assertIsDisplayed()
    }
}
