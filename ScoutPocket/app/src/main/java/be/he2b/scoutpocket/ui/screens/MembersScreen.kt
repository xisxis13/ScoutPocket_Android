package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.he2b.scoutpocket.ui.component.MemberCard
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import be.he2b.scoutpocket.viewmodel.MemberViewModelFactory

@Composable
fun MembersScreen(
    modifier: Modifier = Modifier,
    viewModel: MemberViewModel = viewModel(
        factory = MemberViewModelFactory (LocalContext.current.applicationContext)
    )
) {
    val members = viewModel.members.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Text("Chargement...")
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        } else if (members.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val membersBySection = members.groupBy { it.section }

                membersBySection.forEach { (section, sectionMembers) ->
                    if (sectionMembers.isNotEmpty()) {
                        item {
                            Text(
                                text = section.label,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }

                        items(sectionMembers) { member ->
                            MemberCard(member = member, presence = null)
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Aucun membre trouvé",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    }
}
