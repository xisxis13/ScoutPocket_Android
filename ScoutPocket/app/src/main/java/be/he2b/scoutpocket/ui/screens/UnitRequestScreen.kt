package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.viewmodel.AdminViewModel
import be.he2b.scoutpocket.viewmodel.AdminViewModelFactory
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitRequestsScreen(
    navController: NavController,
    viewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Demandes d'adhésion") })
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
    ) { padding ->
        if (uiState.pendingRequests.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aucune demande en attente.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.pendingRequests) { request ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${request.firstName ?: "Inconnu"} ${request.lastName ?: ""}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Veut rejoindre l'unité",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Row {
                                IconButton(onClick = { viewModel.rejectUser(request.userId) }) {
                                    Icon(Lucide.X, "Refuser", tint = MaterialTheme.colorScheme.error)
                                }
                                IconButton(onClick = { viewModel.approveUser(request.userId) }) {
                                    Icon(Lucide.Check, "Accepter", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}