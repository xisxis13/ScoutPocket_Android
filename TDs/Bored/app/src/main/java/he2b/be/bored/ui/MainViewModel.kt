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
    fun fetchRandomActivity() {
        viewModelScope.launch {
            try {
                val boredResponse = BoredService.boredClient.getRandomActivity()
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