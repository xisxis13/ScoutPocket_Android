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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.navigation.BottomNavItem
import be.he2b.scoutpocket.ui.component.ConnectedButtonGroup
import be.he2b.scoutpocket.ui.component.ExpressiveTextField
import be.he2b.scoutpocket.ui.component.SectionDropdown
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.FileUp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    modifier: Modifier = Modifier,
    viewModel: MemberViewModel,
    navController: NavController,
    initialMode: String = "manual",
    onImportCSV: () -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(if(initialMode.lowercase() == "manual") 0 else 1) }
    val memberIsCreated by viewModel.newMemberIsCreated

    LaunchedEffect(memberIsCreated) {
        if (memberIsCreated) {
            navController.navigate(BottomNavItem.Members.route)
            viewModel.resetMemberCreationState()
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
                    IconButton(onClick = { navController.navigateUp() }) {
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ConnectedButtonGroup(
                options = listOf(
                    stringResource(R.string.member_manual_mode),
                    stringResource(R.string.member_csv_mode),
                ),
                selectedIndex = selectedIndex,
                onIndexSelected = { selectedIndex = it },
                modifier = Modifier.fillMaxWidth(),
            )

            if (selectedIndex == 0) {
                ManualMemberForm(
                    navController = navController,
                    viewModel = viewModel
                )
            } else {
                CSVImportSection(onImportCSV = onImportCSV)
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
private fun ManualMemberForm(
    navController: NavController,
    viewModel: MemberViewModel,
) {
    val memberLastName by viewModel.newMemberLastName
    val memberFirstName by viewModel.newMemberFirstName
    val memberSection by viewModel.newMemberSection
    val memberLastNameError by viewModel.newMemberLastNameError
    val memberFirstNameError by viewModel.newMemberFirstNameError

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.member_personal_info),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        ExpressiveTextField(
            value = memberFirstName,
            onValueChange = { viewModel.newMemberFirstName.value = it },
            label = stringResource(R.string.member_firstname_label),
            leadingIcon = Lucide.User,
            isError = memberFirstNameError != null,
            errorMessage = memberFirstNameError,
        )

        ExpressiveTextField(
            value = memberLastName,
            onValueChange = { viewModel.newMemberLastName.value = it },
            label = stringResource(R.string.member_lastname_label),
            leadingIcon = Lucide.User,
            isError = memberLastNameError != null,
            errorMessage = memberLastNameError,
        )

        // TODO: add option to block 'Unité' section when member creation
        SectionDropdown(
            selectedSection = memberSection,
            onSectionChange = { viewModel.newMemberSection.value = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.resetMemberCreationState()
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
                onClick = { viewModel.createMember() },
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
    }
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
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.csv_format_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.csv_format_rule1))
                Text(stringResource(R.string.csv_format_rule2))
                Text(stringResource(R.string.csv_format_rule3))
                Text(stringResource(R.string.csv_format_rule4))
                Text(stringResource(R.string.csv_format_rule5))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.csv_example_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.csv_example_content),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onImportCSV,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Icon(
                Lucide.FileUp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                stringResource(R.string.csv_select_file),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
