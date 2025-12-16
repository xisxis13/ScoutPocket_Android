package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.he2b.scoutpocket.model.formattedDateLong
import be.he2b.scoutpocket.model.formattedTimeRange
import be.he2b.scoutpocket.ui.component.MemberCard
import be.he2b.scoutpocket.ui.component.SectionPill
import be.he2b.scoutpocket.ui.component.SwitchButton
import be.he2b.scoutpocket.viewmodel.EventViewModel
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.CircleSlash
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.TriangleAlert
import com.composables.icons.lucide.Users

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
    val presences = viewModel.presences.value
    val membersConcerned = viewModel.membersConcerned.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val showEventInformations = viewModel.showEventInformations.value

    LaunchedEffect(showEventInformations, event) {
        if (!showEventInformations && event != null) {
            viewModel.loadMembersConcerned()
            viewModel.loadPresences()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top)
            ) {
                SwitchButton(
                    buttons = listOf("Informations" to showEventInformations, "Présences" to !showEventInformations),
                    onClick = { viewModel.switchEventDetailsView() },
                )

                if (showEventInformations) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
                    ) {
                        SectionPill(section = event.section)

                        Text(
                            text = event.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(15.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(22.dp),
                                    imageVector = Lucide.Calendar,
                                    contentDescription = "Calendrier",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }

                            Text(
                                text = event.formattedDateLong() + " • " + event.formattedTimeRange(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(15.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(22.dp),
                                    imageVector = Lucide.MapPin,
                                    contentDescription = "Épingle",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }

                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        PresenceWidget(
                            totalMembers = membersConcerned.size,
                            presence = presences.size, // faire en sorte que cela affiche le nombre de 'présent'
                        )
                    }
                } else {
                    if (membersConcerned != null && presences != null) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            val membersBySection = membersConcerned.groupBy { it.section }

                            membersBySection.forEach { (section, sectionMembers) ->
                                if (sectionMembers.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = section.label,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }

                                    items(sectionMembers) { member ->
                                        val memberPresence = presences.find { it.memberId == member.id }
                                        MemberCard(
                                            member = member,
                                            presence = memberPresence,
                                            onPresenceClick = if (memberPresence != null) {
                                                { viewModel.updatePresenceStatus(memberPresence.eventId, memberPresence.memberId) }
                                            } else null,
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                            Text("Chargement des présences...")
                        }
                    }
                }
            }
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

@Composable
fun PresenceWidget(
    modifier: Modifier = Modifier,
    totalMembers: Int,
    presence: Int,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(15.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(22.dp),
                    imageVector = Lucide.Users,
                    contentDescription = "Personnes",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Text(
                text = "Présences",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp, 0.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = presence.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = "/$totalMembers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Text(
                        text = "Présent(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}