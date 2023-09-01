package coroutine.section2

import coroutine.printWithThread
import kotlinx.coroutines.*
import java.util.concurrent.Executors

/**
 * CoroutineScope 과 CoroutineContext
 *
 * * CoroutineScope 의 역할
 *     * CoroutineContext 라는 데이터를 보관
 *
 * * CoroutineContext 의 역할
 *     * 코루틴 이름, CoroutineExceptionHandler, CoroutineDispatcher 등 코루틴과 관련된 여러가지 데이터를 갖고 있다.
 *     * Map 과 Set 이 합쳐진 자료 형태(하나의 key 에 하나의 Element 가 매핑)
 *
 * * CoroutineDispatcher
 *     * 코루틴을 스레드에 배정하는 역할
 *     * Dispatchers.Default
 *         * 가장 기본적인 디스패처, CPU 자원을 많이 쓸 때 권장. 별다른 설정이 없으면 이 디스패처가 사용됨.
 *     * Dispathcers.IO
 *         * I/O 작업에 최적화된 디스패처
 *     * Dispatchers.Main
 *         * 보통 UI 컴포넌트를 조작하기 위한 디스패처. 특정 의존성을 갖고 있어야 정상적으로 활용할 수 있다.
 */

// 일반 루틴에서 CoroutineScope 를 직접 만들어서 사용하면 메인 스레드가 종료된 후에는 코루틴이 실행되지 않음.
// 이전까진 runBlocking {} 에서 block 해주었기 때문에 코루틴이 마무리됨.
fun example7_1_1() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    Thread.sleep(1_500L) // 코루틴의 실행을 보장하기 위해 스레드를 멈춰줌
}

// 함수에 suspend 키워드를 붙여주고 join() 함수를 호출하면 job 을 기다림
suspend fun example7_1_2() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread("Job 1")
    }

    job.join()
}


// CoroutineScope 를 이용해 하나의 코루틴 생명주기를 관리 가능
class AsyncLogic {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun doSomething() {
        scope.launch {
            // 코루틴 작업
        }
    }

    fun destroy() {
        scope.cancel()
    }
}

fun example7_2_1() {
    val context = CoroutineName("나만의 코루틴") + Dispatchers.Default
    println(context)
}

suspend fun example7_2_2() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        delay(1_000L)
        printWithThread(coroutineContext)

        coroutineContext + CoroutineName("이름")
    }

    job.join()
}

// 자바의 ExecutorService 를 디스패처로 사용하기
// 해당 방법으로 스레드풀에서 코루틴을 사용할 수 있음.
fun example7_3() {
    CoroutineName("나만의 코루틴") + Dispatchers.Default
    val threadPool = Executors.newSingleThreadExecutor()
    CoroutineScope(threadPool.asCoroutineDispatcher()).launch {
        printWithThread("새로운 코루틴")
    }
}