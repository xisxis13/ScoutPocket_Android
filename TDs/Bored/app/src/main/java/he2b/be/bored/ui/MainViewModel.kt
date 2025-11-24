package he2b.be.bored.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import he2b.be.bored.network.BoredService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val displayedActivity = mutableStateOf("")
    val fetchResult = mutableStateOf(BoredResult.UNINITIALIZED)
    val isFreeOnly = mutableStateOf(false)
    val selectedParticipants = mutableStateOf<Int?>(null)

    fun fetchRandomActivity() {
        viewModelScope.launch {
            try {
                val priceParam = if (isFreeOnly.value) 0.0 else null
                val participantsParam = selectedParticipants.value
                val boredResponse = BoredService.boredClient
                    .getRandomActivity(priceParam, participantsParam)
                Log.i("MainViewModel", boredResponse.toString())
                displayedActivity.value = boredResponse.activity
                fetchResult.value = BoredResult.SUCCESS
            }
            catch (e: Exception) {
                Log.e("MainViewModel", "Error while fetching random activity", e)
                fetchResult.value = BoredResult.ERROR
            }
        }
    }

    enum class BoredResult() {
        SUCCESS,
        ERROR,
        UNINITIALIZED;
    }
}