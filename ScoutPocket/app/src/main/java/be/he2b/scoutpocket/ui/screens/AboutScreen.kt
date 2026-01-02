package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "À propos",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Lucide.ArrowLeft,
                            contentDescription = "Retour",
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = "Ravasco Alexis",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Id: 62664",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Groupe: E112",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}