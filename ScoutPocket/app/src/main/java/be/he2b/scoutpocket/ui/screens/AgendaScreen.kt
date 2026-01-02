package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.ui.component.ConnectedButtonGroup
import be.he2b.scoutpocket.ui.component.EmptyState
import be.he2b.scoutpocket.ui.component.EventCard
import be.he2b.scoutpocket.ui.component.LoadingState
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    agendaViewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
    ),
) {
    val upcomingEvents = agendaViewModel.upcomingEvents.value
    val pastEvents = agendaViewModel.pastEvents.value
    val allEvents = agendaViewModel.allEvents.value

    val isLoading = agendaViewModel.isLoading.value
    val errorMessage = agendaViewModel.errorMessage.value

    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Agenda",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${allEvents.size} évènement${if (allEvents.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        when {
            isLoading -> {
                LoadingState(
                    title = "Chargement des membres",
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            errorMessage != null -> {
                EmptyState(
                    icon = Lucide.CircleAlert,
                    title = "Erreur",
                    subtitle = errorMessage ?: "Une erreur est survenue",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            allEvents.isEmpty() -> {
                EmptyState(
                    icon = Lucide.Calendar,
                    title = "Aucun évènement",
                    subtitle = "Créer votre premier évènement pour commencer",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (upcomingEvents.isNotEmpty()) {
                        item(
                            key = "header_next_event"
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Prochain évènement",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        item {
                            EventCard(
                                event = upcomingEvents.first(),
                                onClick = {
                                    navController.navigate(AppScreen.eventDetailsRoute(upcomingEvents.first().id))
                                },
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        ConnectedButtonGroup(
                            options = listOf("À venir", "Passés"),
                            selectedIndex = selectedIndex,
                            onIndexSelected = { selectedIndex = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (selectedIndex == 0) {
                        item(
                            key = "header_upcoming"
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "À venir",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        val upcomingEventsList = if (upcomingEvents.size > 1) upcomingEvents.drop(1) else emptyList()

                        if (upcomingEventsList.isNotEmpty()) {
                            items(
                                items = upcomingEventsList,
                                key = { "upcoming_${it.id}" },
                            ) { event ->
                                EventCard(
                                    event = event,
                                    onClick = {
                                        navController.navigate(AppScreen.eventDetailsRoute(event.id))
                                    },
                                )
                            }
                        } else {
                            item {
                                EmptyState(
                                    icon = Lucide.Calendar,
                                    title = "Aucun évènement à venir",
                                    subtitle = "Créer un évènement et il apparaitra ici",
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                            }
                        }
                    } else {
                        item(
                            key = "header_past"
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Passés",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        if (pastEvents.isNotEmpty()) {
                            items(
                                items = pastEvents,
                                key = { "past_${it.id}" },
                            ) { event ->
                                EventCard(
                                    event = event,
                                    onClick = {
                                        navController.navigate(AppScreen.eventDetailsRoute(event.id))
                                    },
                                )
                            }
                        } else {
                            item {
                                EmptyState(
                                    icon = Lucide.Calendar,
                                    title = "Aucun évènement passé",
                                    subtitle = "Les évènements passés apparaîtront ici",
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
