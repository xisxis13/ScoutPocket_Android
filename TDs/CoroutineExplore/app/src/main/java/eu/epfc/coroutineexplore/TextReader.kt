package eu.epfc.coroutineexplore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

suspend fun main() {
    val job = GlobalScope.launch {
        val theText = readTextFile("test1.txt")
        println("The text is : $theText")
    }

    job.join()
}

private suspend fun readTextFile(textFileName : String) : String? {
    var content : String? = null
    withContext(Dispatchers.IO) {
        println("read text file $textFileName with context ${Thread.currentThread().name}")

        try {
            val textFile = File(textFileName)
            content = textFile.readText()
        } catch (e: Exception) {
            println("Error reading file $textFileName")
        }
    }

    return content
}