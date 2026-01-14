package be.he2b.scoutpocket

import android.os.SystemClock
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.network.AuthManager
import be.he2b.scoutpocket.ui.screens.LoginScreen
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import be.he2b.scoutpocket.viewmodel.LoginViewModelFactory
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController
    private lateinit var authManager: AuthManager

    @Before
    fun setup() {
        composeTestRule.setContent {
            val context = LocalContext.current
            authManager = AuthManager(context)
            authManager.clearAuth()

            navController = TestNavHostController(context)
            navController.navigatorProvider.addNavigator(ComposeNavigator())

            val loginViewModel = LoginViewModelFactory(context).create(LoginViewModel::class.java)

            NavHost(
                navController = navController,
                startDestination = AppScreen.Login.route
            ) {
                composable(AppScreen.Login.route) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        navController = navController
                    )
                }
                composable(AppScreen.Main.route) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Main Screen Loaded")
                    }
                }
            }
        }
    }

    @After
    fun tearDown() {
        authManager.clearAuth()
    }

    @Test
    fun loginScreenDisplaysAllRequiredElements() {
        composeTestRule.onNodeWithContentDescription("Logo ScoutPocket").assertIsDisplayed()
        composeTestRule.onNodeWithText("ScoutPocket").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bienvenue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mot de passe").assertIsDisplayed()
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).assertExists()
        composeTestRule.onNode(hasText("By pass auth") and hasClickAction()).assertExists()
    }

    @Test
    fun emptyEmailCannotclickbuttonwhenpasswordfilled() {
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password123")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun emptyPasswordCannotClickButtonWhenEmailFilled() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun invalidEmailShowsError() {
        composeTestRule.onNodeWithText("Email").performTextInput("invalid-email")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password123")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()
    }

    @Test
    fun emailWithoutAtShowsValidationError() {
        composeTestRule.onNodeWithText("Email").performTextInput("testexample.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()
    }

    @Test
    fun emailWithoutDomainShowsValidationError() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()
    }

    @Test
    fun emptyEmailShowsValidationErrorWhenSingleCharacterEntered() {
        composeTestRule.onNodeWithText("Email").performTextInput("a")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()
    }

    @Test
    fun passwordVisibilityToggleWorksCorrectly() {
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("myPassword123")

        composeTestRule.onNodeWithContentDescription("Afficher").performClick()

        composeTestRule.onNodeWithContentDescription("Masquer").assertExists()

        composeTestRule.onNodeWithContentDescription("Masquer").performClick()

        composeTestRule.onNodeWithContentDescription("Afficher").assertExists()
    }

    @Test
    fun invalidCredentialsShowsAuthError() {
        composeTestRule.onNodeWithText("Email").performTextInput("wrong@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("wrongpassword")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        SystemClock.sleep(3000)

        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertIsDisplayed()

        composeTestRule.onNodeWithText("Main Screen Loaded").assertDoesNotExist()
    }

    @Test
    fun validCredentialsNavigatesToMainScreen() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("dev5!!")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        SystemClock.sleep(3000)

        composeTestRule.onNodeWithText("Main Screen Loaded").assertIsDisplayed()

        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertDoesNotExist()
    }

    @Test
    fun validCredentialsHidesLoginScreen() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("dev5!!")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        SystemClock.sleep(3000)

        composeTestRule.onNodeWithText("Bienvenue").assertDoesNotExist()
        composeTestRule.onNodeWithText("Email").assertDoesNotExist()
    }

    @Test
    fun duringAuthenticationShowsLoadingText() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("dev5!!")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("Connexion en cours…").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun duringAuthenticationShowsCircularProgressIndicator() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.waitUntil(timeoutMillis = 500) {
            try {
                composeTestRule.onAllNodes(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
                    .fetchSemanticsNodes().isNotEmpty()
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun loginButtonIsDisabledWhenBothFieldsEmpty() {
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsDisabledWhenOnlyEmailFilled() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsDisabledWhenOnlyPasswordFilled() {
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password123")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsEnabledWhenBothFieldsFilled() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()
    }

    @Test
    fun loginButtonIsDisabledWhenEmailIsBlank() {
        composeTestRule.onNodeWithText("Email").performTextInput("   ")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsDisabledWhenPasswordIsBlank() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("   ")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsDisabledWhenBothFieldsAreBlank() {
        composeTestRule.onNodeWithText("Email").performTextInput("   ")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("   ")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun loginButtonIsDisabledDuringLoading() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.waitUntil(timeoutMillis = 500) {
            try {
                composeTestRule.onNode(hasText("Connexion en cours…") and hasClickAction())
                    .assertIsNotEnabled()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    @Test
    fun loginButtonCannotBeClickedWhenDisabled() {
        val buttonNode = composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
        buttonNode.assertIsNotEnabled()

        composeTestRule.onNodeWithText("L'email ne peut pas être vide").assertDoesNotExist()
    }

    @Test
    fun emailFieldAcceptsTextInput() {
        val testEmail = "user@example.com"
        composeTestRule.onNodeWithText("Email").performTextInput(testEmail)

        composeTestRule.onNodeWithText("Email").assertExists()
    }

    @Test
    fun passwordFieldAcceptsTextInput() {
        val testPassword = "password123"
        composeTestRule.onNodeWithText("Mot de passe").performTextInput(testPassword)

        composeTestRule.onNodeWithText("Mot de passe").assertExists()
    }

    @Test
    fun clearingEmailDisablesLoginButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()

        composeTestRule.onNodeWithText("Email").performTextClearance()

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun clearingPasswordDisablesLoginButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()

        composeTestRule.onNodeWithText("Mot de passe").performTextClearance()

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun errorMessageClearsWhenUserTypesNewEmail() {
        composeTestRule.onNodeWithText("Email").performTextInput("invalid")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()

        composeTestRule.onNodeWithText("Email").performTextClearance()
        composeTestRule.onNodeWithText("Email").performTextInput("valid@example.com")

        composeTestRule.onNodeWithText("Email non valide").assertDoesNotExist()
    }

    @Test
    fun errorMessageClearsWhenUserTypesInEmailField() {
        composeTestRule.onNodeWithText("Email").performTextInput("invalid")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()

        composeTestRule.onNodeWithText("Mot de passe").performTextClearance()

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()

        composeTestRule.onNodeWithText("Mot de passe").performTextInput("newpassword")

        composeTestRule.onNodeWithText("Email non valide").assertIsDisplayed()

        composeTestRule.onNodeWithText("Email").performTextClearance()
        composeTestRule.onNodeWithText("Email").performTextInput("valid@example.com")

        composeTestRule.onNodeWithText("Email non valide").assertDoesNotExist()
    }

    @Test
    fun emailFieldNextActionMovesToPassword() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Email").performImeAction()

        composeTestRule.onNodeWithText("Mot de passe").assertExists()
    }

    @Test
    fun passwordFieldDoneActionTriggersLoginWhenFieldsFilled() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@he2b.be")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("dev5!!")

        composeTestRule.onNodeWithText("Mot de passe").performImeAction()

        SystemClock.sleep(3000)
        composeTestRule.onNodeWithText("Main Screen Loaded").assertIsDisplayed()
    }

    @Test
    fun multipleFailedAttemptsShowsErrorEachTime() {
        composeTestRule.onNodeWithText("Email").performTextInput("wrong1@test.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("wrong1")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()
        SystemClock.sleep(3000)
        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertIsDisplayed()

        composeTestRule.onNodeWithText("Email").performTextClearance()
        composeTestRule.onNodeWithText("Mot de passe").performTextClearance()

        composeTestRule.onNodeWithText("Email").performTextInput("wrong2@test.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("wrong2")
        composeTestRule.onNode(hasText("Se connecter") and hasClickAction()).performClick()
        SystemClock.sleep(3000)
        composeTestRule.onNodeWithText("Mauvais login ou mot de passe").assertIsDisplayed()
    }

    @Test
    fun whitespaceOnlyPasswordDisablesButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("   ")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun whitespaceOnlyEmailDisablesButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("   ")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password123")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsNotEnabled()
    }

    @Test
    fun singleCharacterEnablesButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("a")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("b")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()
    }

    @Test
    fun spacesAndCharactersEnablesButton() {
        composeTestRule.onNodeWithText("Email").performTextInput("  a  ")
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("  b  ")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()
    }

    @Test
    fun veryLongEmailEnablesButtonWhenPasswordFilled() {
        val longEmail = "a".repeat(50) + "@example.com"
        composeTestRule.onNodeWithText("Email").performTextInput(longEmail)
        composeTestRule.onNodeWithText("Mot de passe").performTextInput("password")

        composeTestRule.onNode(hasText("Se connecter") and hasClickAction())
            .assertIsEnabled()
    }

}
