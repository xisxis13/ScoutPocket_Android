package be.he2b.scoutpocket

import be.he2b.scoutpocket.viewmodel.LoginViewModel
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        viewModel = LoginViewModel()
    }

    @Test
    fun emailIsValid() {
        viewModel.checkEmail("test@example.com")
        assertTrue(viewModel.isEmailValid.value)
    }

    @Test
    fun emailIsNotValidWithoutAt() {
        viewModel.checkEmail("testexample.com")
        assertFalse(viewModel.isEmailValid.value)
    }

    @Test
    fun emptyEmailIsNotValid() {
        viewModel.checkEmail("")
        assertFalse(viewModel.isEmailValid.value)
    }
}