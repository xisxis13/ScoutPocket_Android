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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    val eventName by viewModel.newEventName.collectAsState()
    val eventSection by viewModel.newEventSection.collectAsState()
    val eventDate by viewModel.newEventDate.collectAsState()
    val eventStartTime by viewModel.newEventStartTime.collectAsState()
    val eventEndTime by viewModel.newEventEndTime.collectAsState()
    val eventLocation by viewModel.newEventLocation.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    val isEventCreated = uiState.isEventCreated
    val eventNameErrorRes = uiState.nameError
    val eventLocationErrorRes = uiState.locationError
    val errorMessageRes = uiState.errorMessage

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    val eventNameError = eventNameErrorRes?.let { stringResource(it) }
    val eventLocationError = eventLocationErrorRes?.let { stringResource(it) }
    val errorMessage = errorMessageRes?.let { stringResource(it) }

    LaunchedEffect(isEventCreated) {
        if (isEventCreated) {
            viewModel.resetEventCreationState()
            navController.navigateUp()
        }
    }

    LaunchedEffect(errorMessageRes) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
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
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.createEvent()
                    }
                ),
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
