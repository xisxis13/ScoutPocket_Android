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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.viewmodel.UnitSetupUiState
import be.he2b.scoutpocket.viewmodel.UnitSetupViewModel
import be.he2b.scoutpocket.viewmodel.UnitSetupViewModelFactory
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Search

@Composable
fun UnitSetupScreen(
    navController: NavController,
    viewModel: UnitSetupViewModel = viewModel(factory = UnitSetupViewModelFactory()),
) {

    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val isSetupComplete = uiState.isSetupComplete

    LaunchedEffect(isSetupComplete) {
        if (isSetupComplete) {
            navController.navigate(AppScreen.Main.name) {
                popUpTo(AppScreen.Login.name) { inclusive = true }
            }
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configuration de l'Unité",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tu dois appartenir à une unité pour continuer.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- ONGLETS ---
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

            Spacer(modifier = Modifier.height(24.dp))

            // --- CONTENU DES ONGLETS ---
            if (selectedTab == 0) {
                CreateUnitForm(viewModel, uiState.isLoading)
            } else {
                JoinUnitForm(viewModel, uiState)
            }

            // --- MESSAGES D'ERREUR OU SUCCÈS ---
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

@Composable
fun CreateUnitForm(viewModel: UnitSetupViewModel, isLoading: Boolean) {
    val name by viewModel.unitNameInput.collectAsState()
    val code by viewModel.unitCodeInput.collectAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Créer une nouvelle unité",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Champ CODE (ID)
        OutlinedTextField(
            value = code,
            onValueChange = { viewModel.unitCodeInput.value = it },
            label = { Text("Code officiel (ex: HD022)") },
            placeholder = { Text("HD022") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        // Champ NOM
        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.unitNameInput.value = it },
            label = { Text("Nom de l'unité") },
            placeholder = { Text("ex: 38ème Lessines") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.createUnit() },
            enabled = !isLoading && name.isNotBlank() && code.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Valider et Démarrer")
            }
        }
    }
}

@Composable
fun JoinUnitForm(viewModel: UnitSetupViewModel, uiState: UnitSetupUiState) {
    val search by viewModel.searchInput.collectAsState()

    Column {
        Text(
            "Rechercher une unité existante",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )

        OutlinedTextField(
            value = search,
            onValueChange = {
                viewModel.searchInput.value = it
                viewModel.searchUnits()
            },
            label = { Text("Rechercher par nom ou code") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = { Icon(Lucide.Search, null) }
        )

        Spacer(Modifier.height(16.dp))

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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = unit.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Code: ${unit.id}",
                                style = MaterialTheme.typography.bodySmall
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
        }
    }
}