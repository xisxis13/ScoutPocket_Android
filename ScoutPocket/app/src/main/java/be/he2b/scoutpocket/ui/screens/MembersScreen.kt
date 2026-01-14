package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import be.he2b.scoutpocket.R
import be.he2b.scoutpocket.ui.component.EmptyState
import be.he2b.scoutpocket.ui.component.LoadingState
import be.he2b.scoutpocket.ui.component.MemberCard
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Users

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MemberViewModel,
) {
    val members = viewModel.members.value
    val membersBySection = members.groupBy { it.section }

    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value
    val importSuccessMessage = viewModel.importSuccessMessage.value

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.members_screen_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(R.string.members_count, members.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        when {
            isLoading -> {
                LoadingState(
                    title = stringResource(R.string.loading_members),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            errorMessage != null -> {
                EmptyState(
                    icon = Lucide.CircleAlert,
                    title = stringResource(R.string.error_general),
                    subtitle = errorMessage ?: stringResource(R.string.error_general),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            members.isEmpty() -> {
                EmptyState(
                    icon = Lucide.Users,
                    title = stringResource(R.string.members_empty_title),
                    subtitle = stringResource(R.string.members_empty_subtitle),
                    modifier = Modifier
                        .padding(paddingValues),
                )
            }

            else -> {
                // TODO: Add searchbar
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(paddingValues),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    membersBySection.forEach { (section, membersInSection) ->
                        item(key = "header_${section.name}") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = section.label,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        items(
                            items = membersInSection,
                            key = { it.id }
                        ) { member ->
                            MemberCard(
                                member = member,
                                presence = null,
                            )
                        }
                    }
                }
            }
        }
    }
}
