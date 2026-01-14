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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.ui.component.ExpressiveDatePicker
import be.he2b.scoutpocket.ui.component.ExpressiveTextField
import be.he2b.scoutpocket.ui.component.ExpressiveTimePicker
import be.he2b.scoutpocket.ui.component.SectionDropdown
import be.he2b.scoutpocket.viewmodel.EventViewModel
import be.he2b.scoutpocket.viewmodel.EventViewModelFactory
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.Type

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(LocalContext.current.applicationContext)
    ),
) {
    val eventName by viewModel.newEventName
    val eventSection by viewModel.newEventSection
    val eventDate by viewModel.newEventDate
    val eventStartTime by viewModel.newEventStartTime
    val eventEndTime by viewModel.newEventEndTime
    val eventLocation by viewModel.newEventLocation
    val eventIsCreated by viewModel.newEventIsCreated

    val eventNameError by viewModel.newEventNameError
    val eventLocationError by viewModel.newEventLocationError

    LaunchedEffect(eventIsCreated) {
        if (eventIsCreated) {
            viewModel.resetEventCreationState()
            navController.navigateUp()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.new_event_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetEventCreationState()
                        navController.navigateUp()
                    }) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.event_general_info),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            ExpressiveTextField(
                value = eventName,
                onValueChange = { viewModel.newEventName.value = it },
                label = stringResource(R.string.event_name_label),
                leadingIcon = Lucide.Type,
                isError = eventNameError != null,
                errorMessage = eventNameError,
            )

            SectionDropdown(
                selectedSection = eventSection,
                onSectionChange = { viewModel.newEventSection.value = it },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.event_date_time),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            ExpressiveDatePicker(
                value = eventDate,
                onValueChange = { viewModel.newEventDate.value = it },
                label = stringResource(R.string.event_date),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ExpressiveTimePicker(
                    value = eventStartTime,
                    onValueChange = { viewModel.newEventStartTime.value = it },
                    label = stringResource(R.string.event_start_time),
                    modifier = Modifier
                        .weight(1f),
                )

                ExpressiveTimePicker(
                    value = eventEndTime,
                    onValueChange = { viewModel.newEventEndTime.value = it },
                    label = stringResource(R.string.event_end_time),
                    modifier = Modifier
                        .weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.event_location),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            ExpressiveTextField(
                value = eventLocation,
                onValueChange = { viewModel.newEventLocation.value = it },
                label = stringResource(R.string.event_location_label),
                leadingIcon = Lucide.MapPin,
                isError = eventLocationError != null,
                errorMessage = eventLocationError,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetEventCreationState()
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(R.string.cancel_button),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Button(
                    onClick = { viewModel.createEvent() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
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

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
