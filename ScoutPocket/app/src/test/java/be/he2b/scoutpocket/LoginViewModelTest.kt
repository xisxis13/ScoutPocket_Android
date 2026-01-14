package be.he2b.scoutpocket

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.he2b.scoutpocket.network.AuthBody
import be.he2b.scoutpocket.network.AuthHTTPClient
import be.he2b.scoutpocket.network.AuthManager
import be.he2b.scoutpocket.network.AuthResponse
import be.he2b.scoutpocket.network.AuthService
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockAuthManager: AuthManager
    private lateinit var mockContext: Context
    private lateinit var mockAuthClient: AuthHTTPClient
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockAuthManager = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)
        mockAuthClient = mockk()

        every { mockContext.getString(R.string.auth_error_invalid_email) } returns "Email non valide"
        every { mockContext.getString(R.string.auth_error_empty_email) } returns "L\'email ne peut pas être vide"
        every { mockContext.getString(R.string.auth_error_empty_password) } returns "Le mot de passe ne peut pas être vide"
        every { mockContext.getString(R.string.auth_error_wrong_credentials) } returns "Mauvais login ou mot de passe"
        every { mockContext.getString(R.string.auth_error_connexion) } returns "Erreur de connexion : impossible de joindre le serveur"

        every { mockAuthManager.isAuthenticated() } returns false
        every { mockAuthManager.getUserEmail() } returns null

        mockkObject(AuthService)
        every { AuthService.authClient } returns mockAuthClient

        viewModel = LoginViewModel(mockAuthManager, mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
        unmockkObject(AuthService)
    }

    private fun createMockAuthResponse(
        accessToken: String = "access_token_123",
        tokenType: String = "bearer",
        expiresIn: Int = 3600,
        expiresAt: Long = System.currentTimeMillis() / 1000 + 3600,
        refreshToken: String = "refresh_token_456",
        user: Any? = null
    ) = AuthResponse(
        accessToken = accessToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        expiresAt = expiresAt,
        refreshToken = refreshToken,
        user = user
    )

    @Test
    fun initShouldCheckAuthentificationStatus() {
        verify { mockAuthManager.isAuthenticated() }
    }

    @Test
    fun initShouldLoadSavedEmailWhenAuthenticated() {
        val savedEmail = "test@example.com"
        every { mockAuthManager.isAuthenticated() } returns true
        every { mockAuthManager.getUserEmail() } returns savedEmail

        val viewModel = LoginViewModel(mockAuthManager, mockContext)

        assertEquals(savedEmail, viewModel.email.value)
        assertTrue(viewModel.isAuthenticated.value)
        verify { mockAuthManager.isAuthenticated() }
        verify { mockAuthManager.getUserEmail() }
    }

    @Test
    fun initShouldNotSetEmailWhenNotAuthenticated() {
        every { mockAuthManager.isAuthenticated() } returns false

        val viewModel = LoginViewModel(mockAuthManager, mockContext)

        assertEquals("", viewModel.email.value)
        assertFalse(viewModel.isAuthenticated.value)
    }

    @Test
    fun updateemailShouldUpdateEmailValue() {
        val newEmail = "test@example.com"

        viewModel.updateEmail(newEmail)

        assertEquals(newEmail, viewModel.email.value)
    }

    @Test
    fun updateemailShouldResetValidationWhenEmailWasInvalid() {
        viewModel.isEmailValid.value = false
        viewModel.errorMessage.value = "Error"

        viewModel.updateEmail("new@email.com")

        assertTrue(viewModel.isEmailValid.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun updatepasswordShouldUpdatePasswordValue() {
        val newPassword = "password123"

        viewModel.updatePassword(newPassword)

        assertEquals(newPassword, viewModel.password.value)
    }

    @Test
    fun updatepasswordShouldResetValidationWhenPasswordWasInvalid() {
        viewModel.isPasswordValid.value = false
        viewModel.errorMessage.value = "Error"

        viewModel.updatePassword("newPassword")

        assertTrue(viewModel.isPasswordValid.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun authenticateShouldFailWithEmptyEmail() {
        viewModel.updateEmail("")
        viewModel.updatePassword("password123")

        viewModel.authenticate()

        assertFalse(viewModel.isEmailValid.value)
        assertEquals("L\'email ne peut pas être vide",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isAuthenticated.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldFailWithInvalidEmailFormat() {
        viewModel.updateEmail("invalid-email")
        viewModel.updatePassword("password123")

        viewModel.authenticate()

        assertFalse(viewModel.isEmailValid.value)
        assertEquals("Email non valide", viewModel.errorMessage.value)
        assertFalse(viewModel.isAuthenticated.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldFailWithEmailWithoutDomain() {
        viewModel.updateEmail("test@")
        viewModel.updatePassword("password123")

        viewModel.authenticate()

        assertFalse(viewModel.isEmailValid.value)
        assertEquals("Email non valide", viewModel.errorMessage.value)
        assertFalse(viewModel.isAuthenticated.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldFailWithEmptyPassword() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("")

        viewModel.authenticate()

        assertFalse(viewModel.isPasswordValid.value)
        assertEquals("Le mot de passe ne peut pas être vide",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isAuthenticated.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldFailWithWhitespaceOnlyPassword() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("    ")

        viewModel.authenticate()

        assertFalse(viewModel.isPasswordValid.value)
        assertEquals("Le mot de passe ne peut pas être vide",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isAuthenticated.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldAcceptValidEmailFormats() = runTest {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "test_123@domain.org",
        )

        validEmails.forEach { email ->
            viewModel.updateEmail(email)
            viewModel.updatePassword("password")

            coEvery {
                mockAuthClient.postAuth(any())
            } returns Response.success(createMockAuthResponse())

            viewModel.authenticate()
            advanceUntilIdle()

            assertTrue(viewModel.isEmailValid.value,
                "Email $email should be valid")

            viewModel.logout()
        }
    }

    @Test
    fun authenticateShouldSucceedWithValidCredentials() = runTest {
        val email = "test@example.com"
        val password = "password123"

        viewModel.updateEmail(email)
        viewModel.updatePassword(password)

        coEvery {
            mockAuthClient.postAuth(AuthBody(email, password))
        } returns Response.success(createMockAuthResponse())

        viewModel.authenticate()
        advanceUntilIdle()

        assertTrue(viewModel.isAuthenticated.value)
        assertNull(viewModel.errorMessage.value)
        assertEquals("", viewModel.password.value)
        assertFalse(viewModel.isLoading.value)

        verify {
            mockAuthManager.saveAuthData(
                accessToken = any(),
                refreshToken = any(),
                expiresAt = any(),
                email = email,
            )
        }
    }

    @Test
    fun authenticateShouldTrimEmailBeforeSending() = runTest {
        val email = "  test@example.com  "
        val trimmedEmail = "test@example.com"
        val password = "password"

        viewModel.updateEmail(email)
        viewModel.updatePassword(password)

        coEvery {
            mockAuthClient.postAuth(AuthBody(trimmedEmail, password))
        } returns Response.success(createMockAuthResponse())

        viewModel.authenticate()
        advanceUntilIdle()

        coEvery {
            mockAuthClient.postAuth(AuthBody(trimmedEmail, password))
        }
    }

    @Test
    fun authenticateShouldSetLoadingStateDuringAuthentification() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        coEvery {
            mockAuthClient.postAuth(any())
        } coAnswers {
            delay(100)
            Response.success(createMockAuthResponse())
        }

        viewModel.authenticate()

        assertTrue(viewModel.isLoading.value)

        advanceUntilIdle()
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldFailWithWrongCredentials() = runTest {
        val email = "test@example.com"
        val password = "wrong_password"

        viewModel.updateEmail(email)
        viewModel.updatePassword(password)

        coEvery {
            mockAuthClient.postAuth(any())
        } returns Response.error(401, "".toResponseBody())

        viewModel.authenticate()
        advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value)
        assertEquals("Mauvais login ou mot de passe",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)

        verify(exactly = 0) {
            mockAuthManager
                .saveAuthData(any(), any(), any(), any())
        }
    }

    @Test
    fun authenticateShouldHandleNetworkError() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        coEvery {
            mockAuthClient.postAuth(any())
        } throws Exception("Network timeout")

        viewModel.authenticate()
        advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value)
        assertEquals("Erreur de connexion : impossible de joindre le serveur",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldHandleNullResponseBody() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        coEvery {
            mockAuthClient.postAuth(any())
        } returns Response.success(null)

        viewModel.authenticate()
        advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value)
        assertEquals("Erreur de connexion : impossible de joindre le serveur",
            viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun authenticateShouldHandle500ServerError() = runTest {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")

        coEvery {
            mockAuthClient.postAuth(any())
        } returns Response.error(500, "".toResponseBody())

        viewModel.authenticate()
        advanceUntilIdle()

        assertFalse(viewModel.isAuthenticated.value)
        assertEquals("Mauvais login ou mot de passe", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun logoutShouldClearAllDataAndResetState() {
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.isAuthenticated.value = true
        viewModel.errorMessage.value = "Some error"

        viewModel.logout()

        assertFalse(viewModel.isAuthenticated.value)
        assertEquals("", viewModel.email.value)
        assertEquals("", viewModel.password.value)
        assertTrue(viewModel.isEmailValid.value)
        assertTrue(viewModel.isPasswordValid.value)

        verify { mockAuthManager.clearAuth() }
    }

    @Test
    fun clearerrorShouldResetErrorMessage() {
        viewModel.errorMessage.value = "Some error"

        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }

}