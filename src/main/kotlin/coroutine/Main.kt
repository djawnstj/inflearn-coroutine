package coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main(): Unit = runBlocking {
    printWithThread("START")
    launch {
        newRoutine()
    }
    yield()
    printWithThread("END")
}

suspend fun newRoutine() {
    val num1 = 1
    val num2 = 2
    yield()
    printWithThread("${num1 + num2}")
}

fun printWithThread(str: Any) {
    println("[${Thread.currentThread().name}] $str")
}

suspend fun apiCall1() :Int {
    delay(1_000L)
    return 1
}

suspend fun apiCall2() :Int {
    delay(2_000L)
    return 2
}

suspend fun apiCall2_1(num: Int) :Int {
    delay(2_000L)
    return num + 2
}