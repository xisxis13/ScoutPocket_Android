package eu.epfc.coroutineexplore

import java.io.File

fun main() {
    val theText = readTextFile("test1.txt")
    println("The text is : $theText")
}

private fun readTextFile(textFileName : String) : String? {
    var content : String? = null
    println("read text file $textFileName with context ${Thread.currentThread().name}")

    try {
        val textFile = File(textFileName)
        content = textFile.readText()
    } catch (e: Exception) {
        println("Error reading file $textFileName")
    }

    return content
}