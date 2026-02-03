package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Unit
import be.he2b.scoutpocket.ui.component.ExpressiveTextField
import be.he2b.scoutpocket.viewmodel.UnitSetupUiState
import be.he2b.scoutpocket.viewmodel.UnitSetupViewModel
import be.he2b.scoutpocket.viewmodel.UnitSetupViewModelFactory
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Binary
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search
import com.composables.icons.lucide.Text
import com.composables.icons.lucide.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSetupScreen(
    navController: NavController,
    viewModel: UnitSetupViewModel = viewModel(factory = UnitSetupViewModelFactory()),
    onCancel: () -> kotlin.Unit,
    onSuccess: () -> kotlin.Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val firstName by viewModel.firstNameInput.collectAsState()
    val lastName by viewModel.lastNameInput.collectAsState()

    val isIdentityKnown = firstName.isNotBlank()

    val isSetupComplete = uiState.isSetupComplete
    val successMessage = uiState.successMessage

    LaunchedEffect(successMessage, isSetupComplete) {
        if (successMessage != null || isSetupComplete) {
            onSuccess()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Configuration",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Tu dois appartenir à une unité pour continuer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Lucide.ArrowLeft,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = { },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!isIdentityKnown) {
                Text("Qui es-tu ?", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(8.dp))

                ExpressiveTextField(
                    value = firstName,
                    onValueChange = { viewModel.firstNameInput.value = it },
                    label = "Prénom",
                    leadingIcon = Lucide.User
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpressiveTextField(
                    value = lastName,
                    onValueChange = { viewModel.lastNameInput.value = it },
                    label = "Nom",
                    leadingIcon = Lucide.User
                )

                Spacer(modifier = Modifier.height(24.dp))

            } else {
                Text(
                    text = "Bonjour $firstName !",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Créer") },
                        icon = { Icon(Lucide.Plus, null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Rejoindre") },
                        icon = { Icon(Lucide.Search, null) }
                    )
                }

                if (selectedTab == 0) {
                    CreateUnitForm(viewModel, uiState.isLoading)
                } else {
                    JoinUnitForm(viewModel, uiState)
                }

                uiState.errorMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(it), color = MaterialTheme.colorScheme.error)
                }

                uiState.successMessage?.let {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CreateUnitForm(viewModel: UnitSetupViewModel, isLoading: Boolean) {
    val name by viewModel.unitNameInput.collectAsState()
    val code by viewModel.unitCodeInput.collectAsState()

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Créer une nouvelle unité",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        ExpressiveTextField(
            value = code,
            onValueChange = {
                viewModel.unitCodeInput.value = it
            },
            label = "Code de l'unité",
            // TODO: Add placeholder
            leadingIcon = Lucide.Binary,
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

        ExpressiveTextField(
            value = name,
            onValueChange = {
                viewModel.unitNameInput.value = it
            },
            label = "Nom de l'unité",
            leadingIcon = Lucide.Text,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    viewModel.createUnit()
                },
            ),
        )

        Button(
            onClick = {
                viewModel.createUnit()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    Lucide.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    stringResource(R.string.create_button),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun JoinUnitForm(
    viewModel: UnitSetupViewModel,
    uiState: UnitSetupUiState
) {
    val search by viewModel.searchInput.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Rechercher une unité existante",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        ExpressiveTextField(
            value = search,
            onValueChange = {
                viewModel.searchInput.value = it
                viewModel.searchUnits()
            },
            label = "Rechercher par nom ou code",
            leadingIcon = Lucide.Search,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
        )

        if (uiState.foundUnits.isEmpty() && search.length > 2) {
            Text(
                "Aucune unité trouvée.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.foundUnits) { unit ->
                UnitCard(
                    unit = unit,
                    uiState = uiState,
                    viewModel = viewModel,
                )
            }
        }
    }
}

@Composable
fun UnitCard(
    unit: Unit,
    viewModel: UnitSetupViewModel,
    uiState: UnitSetupUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Code: ${unit.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { viewModel.requestToJoin(unit) },
                enabled = !uiState.isLoading
            ) {
                Text("Rejoindre")
            }
        }
    }
}
