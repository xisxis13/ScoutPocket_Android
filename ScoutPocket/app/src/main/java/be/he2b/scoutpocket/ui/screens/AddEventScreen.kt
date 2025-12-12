package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AddEventScreen(
    modifier: Modifier = Modifier,
    viewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
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
            .padding(24.dp),
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

            // Spacer(modifier = Modifier.height(8.dp))

            LabeledSelect(
                label = "Section",
                options = Section.entries.map { it.label },
                selected = eventSection.label,
                onSelectedChange = { selectedLabel ->
                    viewModel.newEventSection.value = Section.entries.first { it.label == selectedLabel }
                }
            )

            // Spacer(modifier = Modifier.height(8.dp))

            DatePickerField(
                label = "Date de l'événement",
                selectedDate = eventDate,
                onDateSelected = { newDate ->
                    viewModel.newEventDate.value = newDate
                }
            )

            // Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                label = "Heure de début",
                selectedTime = eventStartTime,
                onTimeSelected = { newTime ->
                    viewModel.newEventStartTime.value = newTime
                }
            )

            // Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                label = "Heure de fin",
                selectedTime = eventEndTime,
                onTimeSelected = { newTime ->
                    viewModel.newEventEndTime.value = newTime
                }
            )

            // Spacer(modifier = Modifier.height(8.dp))

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledSelect(
    label: String,
    options: List<String>,
    selected: String?,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelectedChange(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    selectedDate: java.time.LocalDate,
    onDateSelected: (java.time.LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDate = remember(selectedDate) {
        selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {  },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Lucide.Calendar,
                    contentDescription = "Sélectionner une date"
                )
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(newDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String,
    selectedTime: java.time.LocalTime,onTimeSelected: (java.time.LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }

    val formattedTime = remember(selectedTime) {
        selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = formattedTime,
            onValueChange = {  },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Lucide.Clock,
                    contentDescription = "Sélectionner une heure"
                )
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showTimePicker = true }
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute,
            is24Hour = true
        )

        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onConfirm = {
                val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onTimeSelected(newTime)
                showTimePicker = false
            }
        ) {
            // Vous pouvez choisir entre TimePicker (horloge) ou TimeInput (clavier)
            TimeInput(state = timePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Sélectionner une heure",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

