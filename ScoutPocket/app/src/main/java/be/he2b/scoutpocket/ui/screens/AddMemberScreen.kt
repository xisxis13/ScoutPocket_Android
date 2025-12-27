package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.navigation.BottomNavItem
import be.he2b.scoutpocket.ui.component.LabeledSelect
import be.he2b.scoutpocket.ui.component.SwitchButton
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import com.composables.icons.lucide.FileUp
import com.composables.icons.lucide.Lucide

@Composable
fun AddMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: MemberViewModel,
    navController: NavController,
    initialMode: String = "manual",
    onImportCSV: () -> Unit,
) {
    var importMode by remember { mutableStateOf((initialMode == "import")) }

    val memberIsCreated by viewModel.newMemberIsCreated

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
            SwitchButton(
                buttons = listOf(
                    "Manuel" to !importMode,
                    "Importer un CSV" to importMode,
                ),
                onClick = { importMode = !importMode }
            )

            if (!importMode) {
                ManualMemberForm(viewModel = viewModel)
            } else {
                CSVImportSection(onImportCSV = onImportCSV)
            }
        }
    }
}

@Composable
private fun ManualMemberForm(
    viewModel: MemberViewModel,
) {
    val memberLastName by viewModel.newMemberLastName
    val memberFirstName by viewModel.newMemberFirstName
    val memberSection by viewModel.newMemberSection
    val memberLastNameError by viewModel.newMemberLastNameError
    val memberFirstNameError by viewModel.newMemberFirstNameError

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

@Composable
private fun CSVImportSection(
    onImportCSV: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Format du fichier CSV",
            style = MaterialTheme.typography.labelLarge,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("• 3 colonnes : Nom, Prénom, Section")
            Text("• Délimiteur : virgule (,) ou point-virgule (;)")
            Text("• Sections valides : baladins, louveteaux, éclaireurs, pionniers")
            Text("• Les variantes sont acceptées (baladin, eclaireur, etc.)")
            Text("• Les doublons sont automatiquement ignorés")
        }

        Text(
            text = "Exemple de fichier",
            style = MaterialTheme.typography.labelLarge,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(24.dp),
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "DUPONT,Jean,baladins\nMARTIN,Sophie,louveteaux\nBERNARD,Lucas,eclaireurs",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
            )

            Button(
                onClick = onImportCSV,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Lucide.FileUp, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Sélectionner un fichier CSV")
            }
        }
    }
}
