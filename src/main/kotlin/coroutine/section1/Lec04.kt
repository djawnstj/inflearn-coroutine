package coroutine.section1

import coroutine.printWithThread
import kotlinx.coroutines.*

/**
 * 코루틴의 취소
 */

// 취소에 협조하기
// delay()/yield() 같은 kotlinx.coroutines 패키지의 suspend 함수를 사용하면 cancel() 할 수 있음
fun example4_1(): Unit = runBlocking {
    val job1 = launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1_000L)
        printWithThread("Job 2")
    }

    delay(100)
    job1.cancel()
}

// suspend fun 이 없기 때문에 cancel() 은 launch (job) 이 끝난 후 호출됨
fun example4_1_1(): Unit = runBlocking {
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번째 출력!")
                nextPrintTime += 1_000L
            }
        }
    }

    delay(100L)
    job.cancel()
}

// CoroutineScope 의 isActive 변수를 통해 현재 활성화 상태를 확인해 취소하는 방법
// cancel() 의 상태 변화를 받으려면 서로 다른 스레드에서 job 과 cancel 이 이루어져야 함 (하나의 스레드면 결국 job 이 끝날때까지 cancel() 이 호출되지 못하기 때문)
fun example4_2(): Unit = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                printWithThread("${i++}번째 출력!")
                nextPrintTime += 1_000L
            }

            if (!isActive) { // 취소 신호를 받았는지 확인하는 변수.
                throw CancellationException()
            }
        }
    }

    delay(100L)
    job.cancel()
}

// delay()/yield() 같은 suspend fun 도 cancel() 이 호출되면 CancellationException() 을 던지면서 코루틴을 취소시킴
// 그렇기 때문에 try/catch 로 ex를 잡을 수 있음.(취소되지 않음)
fun example4_3() = runBlocking {
    val job = launch {
        try {
            delay(1_000L)
        } catch (e: CancellationException) {}
        printWithThread("delay에 의해 취소되지 않음.")
    }

    delay(100L)
    printWithThread("취소 시작")
    job.cancel()
}