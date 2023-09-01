package coroutine.section1

import coroutine.apiCall1
import coroutine.apiCall2
import coroutine.apiCall2_1
import coroutine.printWithThread
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 코루틴 빌더와 Job
 */

// runBlocking 은 실행되는 동안 쓰레드를 block 시키기 때문에 delay() 로 인해 "END" 부분까지 넘어가지 않고 기다리게 됨
fun example3_1() {
    runBlocking {
        printWithThread("START")

        launch {
            delay(2_000L)
            printWithThread("LAUNCE END")
        }
    }

    printWithThread("END")
}

fun example3_1_1(): Unit = runBlocking {
    printWithThread("START")

    launch {
        delay(2_000L)
        printWithThread("LAUNCE END")
    }
    printWithThread("END")
}

// 코루틴 launch 시작을 지연 하는 방법
fun example3_2(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }

    delay(1_000L)
    job.start()
}

// 코루틴 launch 종료
fun example3_3(): Unit = runBlocking {
    val job = launch {
        (1..5).forEach {
            printWithThread(it)
            delay(500)
        }
    }

    delay(1_000L)
    job.cancel()
}

// 코루틴은 delay 되는동안 다른 코루틴이 실행됨
// job1 이 delay 를 만나서 job2 가 실행되고 job1 의 delay 가 끝나고 "job1" 이 출력된 후 "job2" 가 출력됨
fun example3_4(): Unit = runBlocking {
    val job1 = launch {
        delay(1_000)
        printWithThread("job1")
    }
//    job1.join() // job1 이 끝날때 까지 다른 루틴 실행하지 않기

    val job2 = launch {
        delay(1_000)
        printWithThread("job2")
    }
}

// async: 결과를 반환함
// await() 함수를 통해 결과값을 반환받을 수 있음.
// api call 같은 곳에서 유용하게 사용
fun example3_5(): Unit = runBlocking {
    val job = async { 3 + 5 }
    val eight = job.await()

    printWithThread(eight)
}

// 여러 api 를 동시에 호출해 소요시간을 최소화 할 수 있다.
fun example3_5_1(): Unit = runBlocking {

    val time = measureTimeMillis {
        val job1 = async { apiCall1() }
        val job2 = async { apiCall2() }

        printWithThread(job1.await() + job2.await())
    }

    printWithThread("소요 시간: $time ms")
}

// 콜백 지옥을 해결할 수 있다.
fun example3_5_2(): Unit = runBlocking {
    /*
    콜백 지옥 ex
    apiCall1(object: Callback {
        apiCall2(object: Callback {
            ...
        }
    })
     */

    val time = measureTimeMillis {
        val job1 = async { apiCall1() }
        val job2 = async { apiCall2_1(job1.await()) }

        printWithThread(job2.await())
    }

    printWithThread("소요 시간: $time ms")
}

// async 에 LAZY 옵션을 주면 await() 의 결과를 기다리게 됨
// start() 함수를 호출하면 바로 실행되기 때문에 동시에 실행
fun example3_5_3(): Unit = runBlocking {

    val time = measureTimeMillis {
        val job1 = async(start = CoroutineStart.LAZY) { apiCall1() }
        val job2 = async(start = CoroutineStart.LAZY) { apiCall2() }

//        job1.start()
//        job2.start()
        printWithThread(job1.await() + job2.await())
    }

    printWithThread("소요 시간: $time ms")
}