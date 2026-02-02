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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.ui.component.ConnectedButtonGroup
import be.he2b.scoutpocket.ui.component.EmptyState
import be.he2b.scoutpocket.ui.component.EventCard
import be.he2b.scoutpocket.ui.component.LoadingState
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Lucide
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    agendaViewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
    ),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState by agendaViewModel.uiState.collectAsState()

    val upcomingEvents = uiState.upcomingEvents
    val pastEvents = uiState.pastEvents
    val allEvents = uiState.allEvents

    val isLoading = uiState.isLoading
    val errorMessageRes = uiState.errorMessage

    var selectedIndex by remember { mutableIntStateOf(0) }

    val retryLabel = stringResource(R.string.error_retry)

    val errorMessageString = errorMessageRes?.let { stringResource(it) }

    LaunchedEffect(errorMessageRes) {
        errorMessageString?.let { message ->
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = retryLabel,
                    duration = SnackbarDuration.Long,
                )

                if (result == SnackbarResult.ActionPerformed) {
                    agendaViewModel.loadEvents()
                }

                agendaViewModel.clearError()
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.agenda_screen_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when (upcomingEvents.size) {
                                0 -> stringResource(R.string.agenda_upcoming_count_zero)
                                1 -> stringResource(R.string.agenda_upcoming_count_one)
                                else -> stringResource(
                                    R.string.agenda_upcoming_count_other,
                                    upcomingEvents.size
                                )
                            },
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
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 150.dp)
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(
                    title = stringResource(R.string.agenda_loading_events),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            allEvents.isEmpty() -> {
                EmptyState(
                    icon = Lucide.Calendar,
                    title = stringResource(R.string.agenda_no_events_title),
                    subtitle = stringResource(R.string.agenda_no_events_subtitle),
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
                                text = stringResource(R.string.agenda_next_event),
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
                            options = listOf(
                                stringResource(R.string.agenda_upcoming_tab),
                                stringResource(R.string.agenda_past_tab),
                            ),
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
                                text = stringResource(R.string.agenda_upcoming_section),
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
                                    title = stringResource(R.string.agenda_no_upcoming_title),
                                    subtitle = stringResource(R.string.agenda_no_upcoming_subtitle),
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
                                text = stringResource(R.string.agenda_past_section),
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
                                    title = stringResource(R.string.agenda_no_past_title),
                                    subtitle = stringResource(R.string.agenda_no_past_subtitle),
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
