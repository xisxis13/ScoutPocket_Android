package he2b.be.bored.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Button(
        modifier = Modifier.padding(8.dp),
        onClick = { mainViewModel.fetchRandomActivity() }
    ) {
        Text("Find a Random Activity")
    }
}