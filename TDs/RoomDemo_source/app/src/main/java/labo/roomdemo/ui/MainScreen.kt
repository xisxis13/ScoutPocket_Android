package labo.roomdemo.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.TextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel : MainScreenViewModel = viewModel()

    Scaffold () {paddingValues ->
        Column {
            NoteList(paddingValues, Modifier.weight(1.0f))
            AddNoteView() {text ->

            }
        }
    }
}

@Composable
fun NoteList(contentPadding: PaddingValues,
             modifier: Modifier){

    LazyColumn(contentPadding = contentPadding, modifier = modifier) {

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteView(addButtonCallback : (String)->Unit){
    val text : MutableState<String> = rememberSaveable{mutableStateOf("")}
    val focusManager = LocalFocusManager.current

    Row (verticalAlignment = Alignment.CenterVertically ,
        modifier = Modifier.padding(16.dp)){

        TextField(value = text.value,
            modifier = Modifier.weight(1.0f),
            onValueChange = {
            text.value = it
        })
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            addButtonCallback(text.value)
            text.value = ""
            focusManager.clearFocus()
        }) {
            Text(text = "add")
        }
    }
}