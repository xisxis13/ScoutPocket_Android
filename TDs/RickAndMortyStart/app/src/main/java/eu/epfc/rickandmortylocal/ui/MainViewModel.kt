package eu.epfc.rickandmortylocal.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import eu.epfc.rickandmortylocal.model.Character
import eu.epfc.rickandmortylocal.model.Repository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(context : Context) : ViewModel() {

    val characters : MutableState<List<CharacterUIData>> = mutableStateOf(listOf())
    val showAlive : MutableState<Boolean> = mutableStateOf(false)
    private var loadCharactersJob : Job? = null

    init {
        loadCharacters(context)
    }

    private fun loadCharacters(context: Context) {
        loadCharactersJob = viewModelScope.launch {
            try {
                Repository.loadCharacters(context)
                // convert the list of Characters to a list of CharacterUIData using "map"
                characters.value = Repository.characters.map {character: Character ->
                    val imageResource = context.resources.getIdentifier("char${character.id}", "drawable", context.packageName)
                    val backgroundColor = when (character.status) {
                        "Alive" -> Color(0xFFDAECFC)
                        "Dead" -> Color(0xFFFFDADA)
                        else -> Color(0xFFDADADA)
                    }
                    CharacterUIData(character.name, character.status, character.species, character.gender, imageResource, backgroundColor)
                }
            } catch (e: CancellationException) {
                println("Task cancelled, data not loaded")
            }
        }
    }

    fun cancelLoadCharacters() {
        loadCharactersJob?.cancel()
    }
}

class MainViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(context) as T
    }
}