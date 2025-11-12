package be.he2b.scoutpocket.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp, 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        var email by remember { mutableStateOf("") }

        Text(
            text = "Connectez-vous",
            style = MaterialTheme.typography.displaySmall,
            modifier = modifier
                .padding(bottom = 16.dp)
        )

        TextField(
            value = email,
            onValueChange = {
                email = it
                viewModel.isEmailValid.value = true
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            isError = !viewModel.isEmailValid.value,
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Button(
            onClick = {
                viewModel.checkEmail(email)

                if (viewModel.isEmailValid.value) {
                    navController.navigate(Screens.Main.name)
                }
            },
            modifier = modifier
                .fillMaxWidth(),
        ) {
            Text("Connexion")
        }

        if (!viewModel.isEmailValid.value) {
            Text("invalid email", color = MaterialTheme.colorScheme.error)
        }
    }
}
