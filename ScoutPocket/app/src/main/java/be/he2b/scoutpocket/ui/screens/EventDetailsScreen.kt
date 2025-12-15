package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.he2b.scoutpocket.viewmodel.EventViewModel
import com.composables.icons.lucide.CircleSlash
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert

@Composable
fun EventDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: EventViewModel,
    eventId: Int,
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val event = viewModel.event.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Chargement de l\'évènement...",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        } else if (errorMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    imageVector = Lucide.TriangleAlert,
                    contentDescription = "Erreur",
                    tint = MaterialTheme.colorScheme.onError,
                )
                Text(
                    text = "Évènement non trouvé.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError,
                )
            }
        } else if (event != null) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    modifier = Modifier
                        .size(36.dp),
                    imageVector = Lucide.CircleSlash,
                    contentDescription = "Non trouvé",
                    tint = MaterialTheme.colorScheme.onError,
                )
                Text(
                    text = "Évènement non trouvé.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onError,
                )
            }
        }
    }
}