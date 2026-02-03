package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.ui.component.ExpressiveTextField
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.EyeOff
import com.composables.icons.lucide.Lock
import com.composables.icons.lucide.LogIn
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mail
import com.composables.icons.lucide.User
import com.composables.icons.lucide.UserPlus

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()

    val isEmailValid = uiState.isEmailValid
    val isPasswordValid = uiState.isPasswordValid
    var isAuthenticated = uiState.isAuthenticated
    val needsUnitSetup = uiState.needsUnitSetup
    val isPendingApproval = uiState.isPendingApproval
    val isLoginMode = uiState.isLoginMode
    val isLoading = uiState.isLoading
    val errorMessageRes = uiState.errorMessage

    val errorMessage = errorMessageRes?.let { stringResource(it) }

    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(isAuthenticated, needsUnitSetup, errorMessage) {
        if (isAuthenticated) {
            navController.navigate(AppScreen.Main.route) {
                popUpTo(AppScreen.Login.name) { inclusive = true }
            }
        }

        if (needsUnitSetup) {
            navController.navigate(AppScreen.UnitSetup.route) {
                popUpTo(AppScreen.Login.name) { inclusive = true }
            }
        }

        if (isPendingApproval) {
            snackbarHostState.showSnackbar("Votre demande est en attente de validation par le staff.")
        }

        errorMessageRes?.let { errorResId ->
            snackbarHostState.showSnackbar(context.getString(errorResId))
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.scoutpocket_s),
                        contentDescription = stringResource(R.string.content_description_logo),
                        modifier = Modifier.fillMaxSize(),
                        colorFilter = ColorFilter.tint(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            blendMode = BlendMode.SrcIn
                        )
                    )

                    Image(
                        painter = painterResource(R.drawable.scoutpocket_foulard),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = stringResource(R.string.login_welcome_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Email
                ExpressiveTextField(
                    value = email,
                    onValueChange = {
                        viewModel.updateEmail(it)
                    },
                    label = stringResource(R.string.login_email_label),
                    leadingIcon = Lucide.Mail,
                    isError = !isEmailValid,
                    enable = !isLoading,
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

                // Password
                ExpressiveTextField(
                    value = password,
                    onValueChange = {
                        viewModel.updatePassword(it)
                    },
                    label = stringResource(R.string.login_password_label),
                    leadingIcon = Lucide.Lock,
                    isError = !isPasswordValid,
                    enable = !isLoading,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.authenticate()
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Lucide.EyeOff else Lucide.Eye,
                                contentDescription = stringResource(
                                    if (passwordVisible) R.string.login_password_hide
                                    else R.string.login_password_show
                                )
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                )

                if (!isLoginMode) {
                    // FirstName
                    ExpressiveTextField(
                        value = firstName,
                        onValueChange = {
                            viewModel.updateFirstName(it)
                        },
                        label = "Prénom",
                        leadingIcon = Lucide.User,
                        enable = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(FocusDirection.Down)
                            }
                        ),
                    )

                    // LastName
                    ExpressiveTextField(
                        value = lastName,
                        onValueChange = {
                            viewModel.updateLastName(it)
                        },
                        label = "Nom de famille",
                        leadingIcon = Lucide.User,
                        enable = !isLoading,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.authenticate()
                            }
                        ),
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.authenticate()
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

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.loading_authentification),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                } else {
                    Icon(
                        imageVector = if (isLoginMode) Lucide.LogIn else Lucide.UserPlus,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isLoginMode) stringResource(R.string.login_button) else "S'inscrire",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            TextButton(
                onClick = {
                    viewModel.toggleMode()
                },
                enabled = !isLoading,
                content = {
                    Text(
                        text = if (isLoginMode)
                            "Pas encore de compte ? Créer un compte"
                        else
                            "Déjà un compte ? Se connecter",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )

            // TODO: Delete for final version
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
}
