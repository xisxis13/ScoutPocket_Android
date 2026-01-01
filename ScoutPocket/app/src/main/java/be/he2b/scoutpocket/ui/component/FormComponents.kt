package be.he2b.scoutpocket.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.backgroundColor
import be.he2b.scoutpocket.model.textColor
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.ChevronUp
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Flag
import com.composables.icons.lucide.Lucide
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ExpressiveTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    it,
                    contentDescription = null,
                    tint = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        },
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        isError = isError,
        supportingText = if (isError && errorMessage != null) {
            { Text(errorMessage) }
        } else {
            null
        },
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun SectionDropdown(
    modifier: Modifier = Modifier,
    selectedSection: Section,
    onSectionChange: (Section) -> Unit,
    label: String = "Section",
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier =  modifier
    ) {
        OutlinedTextField(
            value = selectedSection.label,
            onValueChange = {},
            label = { Text(label) },
            leadingIcon = {
                Icon(
                    Lucide.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { expanded = !expanded },
                ) {
                    Icon(
                        if (expanded) Lucide.ChevronUp else Lucide.ChevronDown,
                        contentDescription = null,
                    )
                }
            },
            readOnly = true,
            singleLine = true,
            shape = MaterialTheme.shapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Section.entries.forEach { section ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = section.backgroundColor()
                            ) {
                                Text(
                                    text = section.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = section.textColor(),
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    },
                    onClick = {
                        onSectionChange(section)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveDatePicker(
    value: LocalDate,
    onValueChange: (LocalDate) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    OutlinedTextField(
        value = value.format(dateFormatter),
        onValueChange = {},
        label = { Text(label) },
        leadingIcon = {
            Icon(
                Lucide.Calendar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        readOnly = true,
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        enabled = false,
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value.atStartOfDay(ZoneId.systemDefault())
                .toInstant().toEpochMilli(),
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant
                                .ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onValueChange(localDate)
                        }
                    },
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                ) {
                    Text("Annuler")
                }
            },
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveTimePicker(
    value: LocalTime,
    onValueChange: (LocalTime) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    OutlinedTextField(
        value = value.format(timeFormatter),
        onValueChange = {},
        label = { Text(label) },
        leadingIcon = {
            Icon(
                Lucide.Clock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        readOnly = true,
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { showTimePicker = true },
        enabled = false,
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = value.hour,
            initialMinute = value.minute,
            is24Hour = true,
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onValueChange(
                            LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute,
                            )
                        )
                        showTimePicker = false
                    }
                ) {
                    Text(
                        text = "OK",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Annuler")
                }
            },
            text = {
                TimePicker(
                    state = timePickerState
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
        )
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
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
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
    selectedTime: LocalTime, onTimeSelected: (LocalTime) -> Unit,
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

