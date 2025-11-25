package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.Screens
import be.he2b.scoutpocket.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navController: NavController,
) {
    val email by remember { viewModel.email }
    val password by remember { viewModel.password }
    val isEmailValid by remember { viewModel.isEmailValid }
    val isAuthenticated by remember { viewModel.isAuthenticated }
    val errorMessage by remember { viewModel.errorMessage }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(Screens.Main.name)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp, 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Connectez-vous",
            style = MaterialTheme.typography.displaySmall,
            modifier = modifier
                .padding(bottom = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = {
                viewModel.email.value = it
                viewModel.isEmailValid.value = true
                viewModel.errorMessage.value = null
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            isError = !isEmailValid,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        TextField(
            value = password,
            onValueChange = {
                viewModel.password.value = it
                viewModel.errorMessage.value = null
            },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            isError = !viewModel.isPasswordValid.value,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Button(
            onClick = {
                viewModel.authenticate(email, password)
            },
            modifier = modifier
                .fillMaxWidth(),
        ) {
            Text("Connexion")
        }

        if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp))
        }
    }
}
