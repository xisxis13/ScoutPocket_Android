package eu.epfc.coroutineexplore

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

suspend fun main() {
    val job = GlobalScope.launch {
        val number1 = 11112690007L
        val number2 = 11112690049L

        var isPrime1 : Deferred<Boolean>
        var isPrime2 : Deferred<Boolean>

        isPrime1 = GlobalScope.async {
            isPrime(number1)
        }

        isPrime2 = GlobalScope.async {
            isPrime(number2)
        }

        println("$number1 is prime : ${isPrime1.await()}, $number2 is prime : ${isPrime2.await()}")
    }

    job.join()
}

suspend fun isPrime(number: Long): Boolean {
    var result = true

    withContext(Dispatchers.Default) {
        println("check number $number with context ${Thread.currentThread().name}")

        if (number < 2) {
            result = false
        }
        else if (number == 2L) {
            result = true
        }
        else if (number % 2 == 0L) {
            result = false
        }
        else {
            val upperLimit : Int = sqrt(number.toDouble()).toInt()
            for (i in 3L until upperLimit step 2) {
                if (number % i == 0L) {
                    result = false
                    break
                }
            }
        }

        println("end check number $number with context ${Thread.currentThread().name}")
    }

    return result
}