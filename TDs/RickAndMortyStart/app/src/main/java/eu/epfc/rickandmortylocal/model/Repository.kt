package eu.epfc.rickandmortylocal.model

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream

object Repository {
    private val _characters : MutableList<Character> = mutableListOf()
    val characters : List<Character>
        get() = _characters
    private const val dataFileName = "characters.json"

    suspend fun loadCharacters(context: Context){
        val contentString = readTextFile(context, dataFileName)
        if (contentString != null){
            _characters.clear()
            val rootJSON = JSONObject(contentString)
            val results = rootJSON.getJSONArray("results")
            for (i in 0 until results.length()){
                val episodeJSON = results.getJSONObject(i)
                val id = episodeJSON.getInt("id")
                val name = episodeJSON.getString("name")
                val status = episodeJSON.getString("status")
                val species = episodeJSON.getString("species")
                val gender = episodeJSON.getString("gender")
                val character = Character(id, name, status, species, gender)
                _characters.add(character)
            }
        }
    }

    private suspend fun readTextFile(context: Context, textFileName : String) : String? {
        return withContext(Dispatchers.IO) {
            delay(5000)
            try {
                //read text file
                val assetManager : AssetManager = context.assets
                val inputStream : InputStream = assetManager.open(textFileName)
                val content = inputStream.bufferedReader().use { it.readText() }
                content
            }
            catch (e: Exception){
                Log.e("Repository", "Error reading file $dataFileName")
                null
            }
        }
    }
}