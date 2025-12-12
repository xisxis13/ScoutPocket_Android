package be.he2b.scoutpocket.ui.screens

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
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory

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

    LaunchedEffect(eventIsCreated) {
        if (eventIsCreated) {
            navController.navigate(AppScreen.Main.name)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { viewModel.newEventName.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom de l'événement") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            )

            // TODO: Ajouter un sélecteur pour la Section
            // TODO: Ajouter un sélecteur pour la Date et les Heures

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = eventLocation,
                onValueChange = { viewModel.newEventLocation.value = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Lieu") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.addEvent()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ajouter l'évènement")
            }
        }
    }

}
