package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.ui.component.DatePickerField
import be.he2b.scoutpocket.ui.component.LabeledSelect
import be.he2b.scoutpocket.ui.component.TimePickerField
import be.he2b.scoutpocket.viewmodel.EventViewModel
import be.he2b.scoutpocket.viewmodel.EventViewModelFactory

@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    viewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(LocalContext.current.applicationContext)
    ),
    navController: NavController,
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

    val isFormValid = eventName.isNotBlank() && eventLocation.isNotBlank()

    LaunchedEffect(eventIsCreated) {
        if (eventIsCreated) {
            navController.navigateUp()
            viewModel.resetEventCreationState()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 120.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = {
                    viewModel.newEventName.value = it
                    if (viewModel.newEventNameError.value != null) {
                        viewModel.newEventNameError.value = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom de l'événement") },
                placeholder = {
                    Text(
                        text = "ex: Réunion classique",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = eventNameError != null,
                supportingText = {
                    if (eventNameError != null) {
                        Text(
                            text = eventNameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            LabeledSelect(
                label = "Section",
                options = Section.entries.map { it.label },
                selected = eventSection.label,
                onSelectedChange = { selectedLabel ->
                    viewModel.newEventSection.value = Section.entries.first { it.label == selectedLabel }
                }
            )

            DatePickerField(
                label = "Date de l'événement",
                selectedDate = eventDate,
                onDateSelected = { newDate ->
                    viewModel.newEventDate.value = newDate
                }
            )

            TimePickerField(
                label = "Heure de début",
                selectedTime = eventStartTime,
                onTimeSelected = { newTime ->
                    viewModel.newEventStartTime.value = newTime
                }
            )

            TimePickerField(
                label = "Heure de fin",
                selectedTime = eventEndTime,
                onTimeSelected = { newTime ->
                    viewModel.newEventEndTime.value = newTime
                }
            )

            OutlinedTextField(
                value = eventLocation,
                onValueChange = {
                    viewModel.newEventLocation.value = it
                    if (viewModel.newEventLocationError.value != null) {
                        viewModel.newEventLocationError.value = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Lieu") },
                placeholder = {
                    Text(
                        text = "ex: Local de l\'unité",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                isError = eventLocationError != null,
                supportingText = {
                    if (eventLocationError != null) {
                        Text(
                            text = eventLocationError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )
        }
    }

}
