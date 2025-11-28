package labo.roomdemo.ui


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import labo.roomdemo.database.NoteItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel : MainScreenViewModel = viewModel()

    Scaffold () {paddingValues ->
        Column {
            NoteList(
                viewModel.noteList.value,
                paddingValues,
                Modifier.weight(1.0f),
                onDeleteClick = { note ->
                    viewModel.deleteNoteInTheDatabase(note)
                },
            )
            AddNoteView() {text ->
                viewModel.addNoteInTheDatabase(text)
            }
        }
    }
}

@Composable
fun NoteList(
    noteList: List<NoteItem>,
    contentPadding: PaddingValues,
    modifier: Modifier,
    onDeleteClick: (NoteItem) -> Unit,
){

    LazyColumn(contentPadding = contentPadding, modifier = modifier) {
        items(noteList.size) { index ->
            val note = noteList[index]

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    text = noteList[index].contentText,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )

                IconButton(
                    onClick = { onDeleteClick(note) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Supprimer la note",
                    )
                }
            }
        }
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