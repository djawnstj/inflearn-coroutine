package coroutine.section1

import coroutine.printWithThread
import kotlinx.coroutines.*

/**
 * 코루틴의 예외 처리와 Job 의 상태 변화
 *
 *  * Life Cycle
 * 1. New
 * 2. Active
 * 3. Completing
 * 4. Completed
 *
 * * 예외 발생시 (Active, Completing 에서)
 * 1. Cancelling
 * 2. Cancelled
 */

// runBlocking 이 부모 코루틴이고 그 아래 job1 (launch{}), job2 (launch{}) 는 자식 코루틴임.
fun example5_1_1() = runBlocking {
    val job1 = launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    val job2 = launch {
        delay(1_000L)
        printWithThread("Job 2")
    }
}

// 루트 코루틴 만들기
// CoroutineScope(Dispatchers.Default) 을 통해 새로운 루트 코루틴을 만들고 스레드를 배정해줌
fun example5_1_2() = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 2")
    }
}

// 루트 코루틴에서 launch 안에서 예외가 던져지면 루틴이 종료됨
fun example5_2_1(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).launch {
        throw IllegalStateException()
        // Exception in thread "DefaultDispatcher-worker-1 @coroutine#2"
    }

    delay(1_000L)
}

// 루트 코루틴에서 async 안에서 예외가 던져지면 정상 종료 되고 await() 를 호출하면 던져진 예외를 확인할 수 있음
// await() 가 호출된 스레드에서 스택이 시작되고 어느 코루틴에서 던져졌는지 스택에서 확인 가능.
fun example5_2_2(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalStateException()
    }

    delay(1_000L)
    job.await()
}

// 자식 코루틴의 예외는 부모 코루틴에 전파됨(발생한 예외가 CancellationException 인 경우 취소로 간주하고 부모 코루틴에게 전파하지 않음)
// 내부적으로는 취소나 실패 모두 '취소됨' 상태로 관리
// runBlocking 코루틴은 예외 발생시 종료됨
fun example5_2_3(): Unit = runBlocking {
    val job = async {
        throw IllegalStateException()
    }

    delay(1_000L)
}


// async 의 예외를 부모 코루틴에 전파하지 않는 방법은 async(SupervisorJob()) 로 선언
fun example5_2_4(): Unit = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalStateException()
    }

    delay(1_000L)
}

// 예외 처리 객체 - CoroutineExceptionHandler
// launch 의 인자로 넘겨 사용
// launch 에만 사용 가능
// 부모 코루틴이 있으면 동작하지 않음
// try/catch 와 차이점
// 코루틴 내부에서 try/catch 로 예외를 잡을 경우 예외가 발생하지 않은것으로 간주
fun example5_1(): Unit = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, t ->
        printWithThread("[CoroutineExceptionHandler]: ${t.message}")
    }

    CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException("예외 발생")
    }

    delay(1_000L)
}