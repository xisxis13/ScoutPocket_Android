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
import be.he2b.scoutpocket.navigation.BottomNavItem
import be.he2b.scoutpocket.ui.component.LabeledSelect
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import be.he2b.scoutpocket.viewmodel.MemberViewModelFactory

@Composable
fun AddMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: MemberViewModel = viewModel(
        factory = MemberViewModelFactory(LocalContext.current.applicationContext)
    ),
    navController: NavController,
) {
    val memberLastName by viewModel.newMemberLastName
    val memberFirstName by viewModel.newMemberFirstName
    val memberSection by viewModel.newMemberSection
    val memberIsCreated by viewModel.newMemberIsCreated

    val memberLastNameError by viewModel.newMemberLastNameError
    val memberFirstNameError by viewModel.newMemberFirstNameError

    LaunchedEffect(memberIsCreated) {
        if (memberIsCreated) {
            navController.navigate(BottomNavItem.Members.route)
            viewModel.resetMemberCreationState()
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
                value = memberLastName,
                onValueChange = {
                    viewModel.newMemberLastName.value = it
                    if (viewModel.newMemberLastNameError.value != null) {
                        viewModel.newMemberLastNameError.value = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom de famille") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = memberLastNameError != null,
                supportingText = {
                    if (memberLastNameError != null) {
                        Text(
                            text = memberLastNameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            OutlinedTextField(
                value = memberFirstName,
                onValueChange = {
                    viewModel.newMemberFirstName.value = it
                    if (viewModel.newMemberFirstNameError.value != null) {
                        viewModel.newMemberFirstNameError.value = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Prénom") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = memberFirstNameError != null,
                supportingText = {
                    if (memberFirstNameError != null) {
                        Text(
                            text = memberFirstNameError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )

            LabeledSelect(
                label = "Section",
                options = Section.entries.filter { it != Section.UNITE }.map { it.label },
                selected = memberSection.label,
                onSelectedChange = { selectedLabel ->
                    viewModel.newMemberSection.value = Section.entries.first { it.label == selectedLabel }
                }
            )
        }
    }
}