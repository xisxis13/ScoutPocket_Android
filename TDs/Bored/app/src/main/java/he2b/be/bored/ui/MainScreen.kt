package he2b.be.bored.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen() {
    val viewModel : MainViewModel = viewModel()
    Scaffold {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.padding(128.dp))

                MessageDisplay(viewModel)
                UserControls(viewModel)
            }
        }
    }
}

@Composable
fun MessageDisplay(mainViewModel: MainViewModel) {
    when (mainViewModel.fetchResult.value) {
        MainViewModel.BoredResult.ERROR -> {
            Text(
                text = "Error while fetching random activity",
                modifier = Modifier
                    .height(128.dp)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.LightGray,
            )
        }
        MainViewModel.BoredResult.UNINITIALIZED -> {
            Text(
                text = "Click on the button to find a random activity",
                modifier = Modifier
                    .height(128.dp)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.LightGray,
            )
        }
        else -> {
            Text(
                text = mainViewModel.displayedActivity.value,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                maxLines = 3,
                modifier = Modifier
                    .height(128.dp)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun UserControls(mainViewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val participantsOptions = listOf(1,2,3,4)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = { mainViewModel.fetchRandomActivity() }
        ) {
            Text("Find a Random Activity")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = mainViewModel.isFreeOnly.value,
                onCheckedChange = { isChecked ->
                    mainViewModel.isFreeOnly.value = isChecked
                }
            )
            Text("Only free activities")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = "Participants: ",
                modifier = Modifier.padding(end = 8.dp),
            )

            Box {
                Button(
                    onClick = { expanded = true }
                ) {
                    Text(text = mainViewModel.selectedParticipants.value?.toString() ?: "Any")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Any") },
                        onClick = {
                            mainViewModel.selectedParticipants.value = null
                            expanded = false
                        }
                    )

                    participantsOptions.forEach { number ->
                        DropdownMenuItem(
                            text = { Text(number.toString()) },
                            onClick = {
                                mainViewModel.selectedParticipants.value = number
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}