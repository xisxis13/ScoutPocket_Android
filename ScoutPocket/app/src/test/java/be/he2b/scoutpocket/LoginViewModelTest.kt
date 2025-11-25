package be.he2b.scoutpocket

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.he2b.scoutpocket.network.AuthBody
import be.he2b.scoutpocket.network.AuthHTTPClient
import be.he2b.scoutpocket.network.AuthResponse
import be.he2b.scoutpocket.network.AuthService
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import io.mockk.mockk
import kotlinx.coroutines.test.resetMain
import org.junit.After
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LoginViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockAuthClient: AuthHTTPClient

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockAuthClient = mockk()
        AuthService.authClient = mockAuthClient
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun emailIsValid() {
        val email = "test@example.com"
        viewModel.checkEmail(email)
        assertTrue(viewModel.isEmailValid.value,
            "L'email devrait être considéré comme valide")
        assertNull(viewModel.errorMessage.value,
            "Aucun message d'erreur ne devrait être présent")
    }

    @Test
    fun emailIsNotValidWithoutAt() {
        val email = "testexample.com"
        viewModel.checkEmail(email)
        assertFalse(viewModel.isEmailValid.value,
            "L'email devrait être considéré comme invalide")
        assertEquals("Email non valide", viewModel.errorMessage.value)
    }

    @Test
    fun emptyEmailIsNotValid() {
        val email = ""
        viewModel.checkEmail(email)
        assertFalse(viewModel.isEmailValid.value,
            "Un email vide devrait être considéré comme invalide")
        assertEquals("Email non valide", viewModel.errorMessage.value)
    }

    @Test
    fun authenticatedWithValidCredentialsShouldSucceed() {
        val email = "test@he2b.be"
        val password = "dev5!!"
        val authBody = AuthBody(email, password)
        coEvery { mockAuthClient.postAuth(authBody) } returns Response
            .success(mockk<AuthResponse>(relaxed = true))

        viewModel.authenticate(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.isAuthenticated.value,
            "L'utilisateur devrait être authentifié")
        assertNull(viewModel.errorMessage.value,
            "Aucun message d'erreur ne devrait être présent")
        coVerify(exactly = 1) { mockAuthClient.postAuth(authBody) }
    }

    @Test
    fun authenticatedWithInvalidCredentialsShouldFail() {
        val email = "test@example.be"
        val password = "wrongPassword"
        val authBody = AuthBody(email, password)
        coEvery { mockAuthClient.postAuth(authBody) } returns Response
            .error(401, mockk(relaxed = true))

        viewModel.authenticate(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value,
            "L'utilisateur ne devrait pas être authentifié")
        assertEquals("Mauvais login ou mot de passe",
            viewModel.errorMessage.value)
        coVerify(exactly = 1) { mockAuthClient.postAuth(authBody) }
    }

    @Test
    fun authenticatedWithNetworkErrorShouldFail() {
        val email = "test@he2b.be"
        val password = "dev5!!"
        val authBody = AuthBody(email, password)
        coEvery { mockAuthClient.postAuth(authBody) } throws Exception("Network timeout")

        viewModel.authenticate(email, password)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value,
            "L'utilisateur ne devrait pas être authentifié")
        assertEquals("Erreur de connexion : impossible de joindre le serveur.",
            viewModel.errorMessage.value)
        coVerify(exactly = 1) { mockAuthClient.postAuth(authBody) }
        }
}