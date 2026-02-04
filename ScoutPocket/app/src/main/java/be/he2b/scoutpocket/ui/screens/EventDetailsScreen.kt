package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.database.entity.Presence
import be.he2b.scoutpocket.model.PresenceStatus
import be.he2b.scoutpocket.model.formattedDateLong
import be.he2b.scoutpocket.model.formattedTimeRange
import be.he2b.scoutpocket.ui.component.ConnectedButtonGroup
import be.he2b.scoutpocket.ui.component.EmptyState
import be.he2b.scoutpocket.ui.component.LoadingState
import be.he2b.scoutpocket.ui.component.MemberCard
import be.he2b.scoutpocket.ui.component.SectionPill
import be.he2b.scoutpocket.ui.component.getSegmentedShape
import be.he2b.scoutpocket.viewmodel.EventViewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: EventViewModel,
    eventId: String,
) {
    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val uiState by viewModel.uiState.collectAsState()

    val event = uiState.event
    val presences = uiState.presences
    val membersConcerned = uiState.membersConcerned
    val membersBySection = remember(membersConcerned) {
        membersConcerned.groupBy { it.section }
            .mapValues { (_, list) ->
                list.sortedWith(compareBy({ it.lastName }, { it.firstName }))
            }
            .toSortedMap()
    }
    val totalMembersPresent = uiState.totalMembersPresent
    val isLoading = uiState.isLoading
    val errorMessageRes = uiState.errorMessage

    var selectedIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val errorMessage = errorMessageRes?.let { stringResource(it) }

    LaunchedEffect(errorMessageRes) {
        errorMessage?.let {
            if (event != null && !isLoading) {
                snackbarHostState.showSnackbar(
                    message = it,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.event_details_title),
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
                // TODO: Add a settings button (delete, update, ...)
                actions = { },
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
        },
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingState(
                    title = stringResource(R.string.loading_event),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            errorMessage != null && event == null -> {
                EmptyState(
                    icon = Lucide.CircleAlert,
                    title = stringResource(R.string.error_general),
                    subtitle = errorMessage,
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            event == null -> {
                EmptyState(
                    icon = Lucide.CircleAlert,
                    title = stringResource(R.string.event_not_found_title),
                    subtitle = stringResource(R.string.event_not_found_subtitle),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            else -> {
                if (selectedIndex == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
                    ) {
                        ConnectedButtonGroup(
                            options = listOf(
                                stringResource(R.string.event_details_infos_tab),
                                stringResource(R.string.event_details_presences_tab),
                            ),
                            selectedIndex = selectedIndex,
                            onIndexSelected = { selectedIndex = it },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SectionPill(section = event.section)

                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        // Date
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.Calendar,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.event_detail_date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = event.formattedDateLong(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Time
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.Clock,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.event_detail_time),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = event.formattedTimeRange(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        // Location
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.size(48.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Lucide.MapPin,
                                        contentDescription = null,
                                        modifier = Modifier.size(22.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.event_detail_location),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    text = event.location,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        PresenceWidget(
                            totalMembers = membersConcerned.size,
                            presence = totalMembersPresent,
                            onClick = { selectedIndex = 1 },
                        )
                    }
                } else {
                    val presencesMap = remember(presences) {
                        presences.associateBy { it.memberId }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(paddingValues),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 160.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        item {
                            ConnectedButtonGroup(
                                options = listOf(
                                    "Informations",
                                    "Présences",
                                ),
                                selectedIndex = selectedIndex,
                                onIndexSelected = { selectedIndex = it },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            PresenceWidget(
                                totalMembers = membersConcerned.size,
                                presence = totalMembersPresent,
                                onClick = { },
                            )
                        }

                        membersBySection.forEach { (section, membersInSection) ->
                            item(key = "header_${section.name}") {
                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = section.label,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            itemsIndexed(
                                items = membersInSection,
                                key = { _, member -> member.id }
                            ) { index, member ->

                                val presence = presencesMap[member.id]
                                val itemShape = getSegmentedShape(index, membersInSection.size)
                                val currentStatus = presence?.status ?: PresenceStatus.DEFAULT

                                Column {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        shape = itemShape,
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        shadowElevation = 0.dp
                                    ) {
                                        val displayPresence = presence ?: Presence(
                                            eventId = eventId,
                                            memberId = member.id,
                                            status = PresenceStatus.DEFAULT,
                                        )

                                        MemberCard(
                                            member = member,
                                            presence = displayPresence,
                                            onPresenceClick = if (event.date >= LocalDate.now()) {
                                                { viewModel.updatePresenceStatus(eventId, member.id) }
                                            } else {
                                                if (currentStatus != PresenceStatus.DEFAULT) { {} } else null
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresenceWidget(
    totalMembers: Int,
    presence: Int,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.presences_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        if (presence > 1) R.string.members_present_plural else R.string.members_present_singular
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = presence.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = "/$totalMembers",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}