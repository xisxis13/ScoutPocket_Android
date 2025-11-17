package he2b.be.bored.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import he2b.be.bored.network.BoredService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    fun fetchRandomActivity() {
        viewModelScope.launch {
            val boredResponse = BoredService.boredClient.getRandomActivity()
            Log.i("MainViewModel", boredResponse.toString())
        }
    }
}