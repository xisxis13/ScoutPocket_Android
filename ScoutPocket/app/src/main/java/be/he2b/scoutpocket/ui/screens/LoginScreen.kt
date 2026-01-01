package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.ui.component.ExpressiveTextField
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navController: NavController,
) {
    val email by viewModel.email
    val password by viewModel.password
    val isEmailValid by viewModel.isEmailValid
    val isPasswordValid by viewModel.isPasswordValid
    val isAuthenticated by viewModel.isAuthenticated
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading

    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate(AppScreen.Main.name) {
                popUpTo(AppScreen.Login.name) { inclusive = true }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier
                    .size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                // TODO: Change by ScoutPocket logo
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "Logo ScoutPocket",
                    modifier = Modifier
                        .padding(24.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ScoutPocket",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Bienvenue",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            ExpressiveTextField(
                value = email,
                onValueChange = {
                    viewModel.email.value = it
                    viewModel.isEmailValid.value = true
                },
                label = "Mail",
                leadingIcon = Lucide.Mail,
                isError = !isEmailValid,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExpressiveTextField(
                value = password,
                onValueChange = {
                    viewModel.password.value = it
                    viewModel.isPasswordValid.value = true
                },
                label = "Mot de passe",
                leadingIcon = Lucide.Lock,
                isError = !isPasswordValid,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.authenticate(email, password)
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Lucide.EyeOff else Lucide.Eye,
                            contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Lucide.CircleAlert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.authenticate(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(
                        imageVector = Lucide.LogIn,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Se connecter",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(AppScreen.Main.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            ) {
                Text(
                    text = "By pass auth",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

        }
    }

//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(40.dp, 24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center,
//    ) {
//        WelcomeHeader()
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(
//                    boxColor,
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .border(
//                    width = 1.dp,
//                    color = BorderColor,
//                    shape = RoundedCornerShape(24.dp)
//                )
//                .padding(24.dp)
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = stringResource(R.string.email_label),
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.onBackground,
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                TextField(
//                    value = email,
//                    onValueChange = {
//                        viewModel.email.value = it
//                        viewModel.isEmailValid.value = true
//                        viewModel.errorMessage.value = null
//                    },
//                    label = null,
//                    placeholder = {
//                        Text(
//                            text = stringResource(R.string.email_placeholder),
//                            color = MaterialTheme.colorScheme.onSurface,
//                        )
//                    },
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Email,
//                        imeAction = ImeAction.Next,
//                    ),
//                    isError = !isEmailValid,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(
//                            color = MaterialTheme.colorScheme.surface,
//                            shape = BorderShape,
//                        )
//                        .border(
//                            width = 1.dp,
//                            color = BorderColor,
//                            shape = BorderShape,
//                        ),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        disabledIndicatorColor = Color.Transparent,
//                        errorIndicatorColor = Color.Transparent,
//
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent,
//                        disabledContainerColor = Color.Transparent,
//                        errorContainerColor = Color.Transparent,
//
//                        focusedTextColor = MaterialTheme.colorScheme.primary,
//                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
//                    ),
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(
//                    text = stringResource(R.string.password_label),
//                    style = MaterialTheme.typography.labelLarge,
//                    color = MaterialTheme.colorScheme.onBackground,
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                TextField(
//                    value = password,
//                    onValueChange = {
//                        viewModel.password.value = it
//                        viewModel.errorMessage.value = null
//                    },
//                    label = null,
//                    placeholder = {
//                        Text(
//                            text = stringResource(R.string.password_placeholder),
//                            color = MaterialTheme.colorScheme.onSurface,
//                        )
//                    },
//                    visualTransformation = PasswordVisualTransformation(),
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Email,
//                        imeAction = ImeAction.Done,
//                    ),
//                    isError = !viewModel.isPasswordValid.value,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(
//                            color = boxColor,
//                            shape = BorderShape,
//                        )
//                        .border(
//                            width = 1.dp,
//                            color = BorderColor,
//                            shape = BorderShape,
//                        ),
//                    colors = TextFieldDefaults.colors(
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent,
//                        disabledIndicatorColor = Color.Transparent,
//                        errorIndicatorColor = Color.Transparent,
//
//                        focusedContainerColor = Color.Transparent,
//                        unfocusedContainerColor = Color.Transparent,
//                        disabledContainerColor = Color.Transparent,
//                        errorContainerColor = Color.Transparent,
//
//                        focusedTextColor = MaterialTheme.colorScheme.primary,
//                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
//                    ),
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    viewModel.authenticate(email, password)
//                },
//                shape = BorderShape,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary,
//                    contentColor = MaterialTheme.colorScheme.onPrimary,
//                ),
//                modifier = Modifier
//                    .fillMaxWidth(),
//            ) {
//                Text(
//                    text = stringResource(R.string.login_button),
//                    color = MaterialTheme.colorScheme.onPrimary,
//                    style = MaterialTheme.typography.labelLarge,
//                )
//            }
//
//            if (errorMessage != null) {
//                Text(
//                    text = errorMessage!!,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 8.dp),
//                    textAlign = TextAlign.Center,
//                )
//            }
//        }
//
//        // TODO: delete it for final version
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                navController.navigate(AppScreen.Main.route)
//            },
//            shape = BorderShape,
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            ),
//            modifier = Modifier
//                .fillMaxWidth(),
//        ) {
//            Text(
//                text = "By pass auth",
//                color = MaterialTheme.colorScheme.onPrimaryContainer,
//                style = MaterialTheme.typography.labelLarge,
//            )
//        }
//    }
}

@Composable
fun WelcomeHeader(modifier: Modifier = Modifier) {
    val welcomeTemplate = stringResource(R.string.welcome_message)
    val appName = stringResource(R.string.app_name)

    val annotatedText = buildAnnotatedString {
        val fullText = welcomeTemplate.format(appName)
        val start = fullText.indexOf(appName)
        val end = start + appName.length

        append(fullText)

        addStyle(
            style = SpanStyle(color = MaterialTheme.colorScheme.primary),
            start = start,
            end = end,
        )
    }

    Text(
        text = annotatedText,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
    )
}
