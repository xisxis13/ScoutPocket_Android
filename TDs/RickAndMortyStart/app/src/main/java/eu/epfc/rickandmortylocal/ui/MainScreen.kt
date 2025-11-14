package eu.epfc.rickandmortylocal.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.epfc.rickandmortylocal.R

@Composable
fun MainScreen() {

    val context = LocalContext.current.applicationContext
    val viewModelFactory = MainViewModelFactory(context)
    val mainViewModel : MainViewModel = viewModel(factory = viewModelFactory)

    LaunchedEffect(key1 = mainViewModel.showAlive.value){
        if (mainViewModel.showAlive.value) {
            Toast.makeText(context, "Show Alive : On", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Row (verticalAlignment = Alignment.CenterVertically) {
            // this button is removed in the end of the lab
//            Button(modifier = Modifier.padding(8.dp),
//                onClick = {mainViewModel.loadCharacters(context)}) {
//                Text("Load Characters")
//            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.show_alive))
            Checkbox(checked = mainViewModel.showAlive.value, onCheckedChange = {
                mainViewModel.showAlive.value = it
            })
        }

        LazyColumn(){
            items(mainViewModel.characters.value.size) { index ->
                CharacterItem(mainViewModel.characters.value[index], mainViewModel.showAlive.value)
            }
        }
    }
}

@Composable
fun CharacterItem(character: CharacterUIData, showBackgroundColor : Boolean) {
    val finalColor = if (showBackgroundColor) character.backgroundColor else Color(0xFFDADADA)
    Card(colors = CardDefaults.cardColors(finalColor), modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = character.imageResource) ,
                contentDescription = "Character image",
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp),
                )

            Spacer(modifier = Modifier.padding(8.dp))
            Column(verticalArrangement = Arrangement.Center) {

                Text(character.name, fontSize = 25.sp)
                Row {
                    Text(character.species)
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(character.status)
                }

            }
        }
    }
}

@Preview
@Composable
fun CharacterItemPreview(){
    val character = CharacterUIData("Rick Sanchez", "Alive", "Human", "Human", R.drawable.char1, Color.Green)
    CharacterItem(character =character, false )
}
