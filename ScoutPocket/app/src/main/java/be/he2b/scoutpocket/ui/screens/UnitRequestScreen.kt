package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.ui.component.EmptyState
import be.he2b.scoutpocket.ui.theme.StateAbsentBackground
import be.he2b.scoutpocket.ui.theme.StateAbsentContent
import be.he2b.scoutpocket.ui.theme.StatePresentBackground
import be.he2b.scoutpocket.ui.theme.StatePresentContent
import be.he2b.scoutpocket.viewmodel.AdminViewModel
import be.he2b.scoutpocket.viewmodel.AdminViewModelFactory
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users
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
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Demandes d'adhésion",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Lucide.ArrowLeft,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
    ) { padding ->
        if (uiState.pendingRequests.isEmpty()) {
            EmptyState(
                icon = Lucide.Users,
                title = "Aucune demande d'adhésion",
                subtitle = "Les demandes d'adhésion à votre unité apparaitront ici",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.pendingRequests) { request ->
                    RequestCard(
                        userFirstName = request.firstName,
                        userLastName = request.lastName,
                        onReject = { viewModel.rejectUser(request.userId) },
                        onApprove = { viewModel.approveUser(request.userId) },
                    )
                }
            }
        }
    }
}

@Composable
fun RequestCard(
    userFirstName: String?,
    userLastName: String?,
    onApprove: () -> Unit,
    onReject: () -> Unit,
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
                    text = "${userFirstName ?: "Inconnu"} ${userLastName ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Veut rejoindre l'unité",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(
                    onClick = onReject,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = StateAbsentContent,
                        containerColor = StateAbsentBackground,
                        disabledContentColor = StateAbsentContent.copy(0.5f),
                        disabledContainerColor = StateAbsentBackground.copy(0.5f),
                    ),
                ) {
                    Icon(
                        imageVector = Lucide.X,
                        contentDescription = "Refuser",
                    )
                }
                IconButton(
                    onClick = onApprove,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = StatePresentContent,
                        containerColor = StatePresentBackground,
                        disabledContentColor = StatePresentContent.copy(0.5f),
                        disabledContainerColor = StatePresentBackground.copy(0.5f),
                    ),
                ) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = "Accepter",
                        tint = StatePresentContent,
                    )
                }
            }
        }
    }
}
