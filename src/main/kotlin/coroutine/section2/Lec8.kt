package coroutine.section2

import coroutine.printWithThread
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

/**
 * suspending function
 * * suspending function
 *     - suspend 가 붙은 함수
 *     - 다른 suspend 함수를 호출할 수 있다.
 *     - launch() 함수의 마지막 파라미터인 block 파라미터가 suspending 람다이기 때문에 suspend 가 아닌 함수에서 사용 가능(delay() 등)
 *     - 정지/중지/유예 기능을 가짐
 */

suspend fun a() {
    printWithThread("A")
}

suspend fun b() {
    printWithThread("B")
}

suspend fun c() {
    printWithThread("C")
}

// suspend 함수가 중지되는 지점이 아닌 중지될 수 있는 지점임을 보여주는 예제
// suspend 함수인 a(), b(), c() 를 호출했지만 차례대로 A, B, C 가 출력됨
fun example8_1(): Unit = runBlocking {
    launch {
        a()
        b()
    }

    launch {
        c()
    }
}

fun call1_1(): Int {
    Thread.sleep(1_000L)
    return 100
}

fun call2_1(num: Int): Int {
    Thread.sleep(1_000L)
    return num * 2
}

// 부모 함수 (example8_2_1) 입장에선 async 의 반환 타입인 Deferred 에 의존하게 됨
// 이 경우 코루틴이 아닌 다른 클래스로 변경시 example8_2_1 까지 영향이 전파됨
fun example8_2_1(): Unit = runBlocking {
    val result1 = async {
        call1_1()
    }

    val result2 = async {
        call2_1(result1.await())
    }

    printWithThread(result2.await())
}

suspend fun call1_2(): Int {
    return CoroutineScope(Dispatchers.Default).async {
        Thread.sleep(1_000L)
        100
    }.await()
}

suspend fun call2_2(num: Int): Int {
    return CompletableFuture.supplyAsync {
        Thread.sleep(1_000L)
        num * 2
    }.await()
}

// 호출할 함수를 suspend 함수로 변경하여 부모 함수(example8_2_2) 는 다른 클래스에 의존하지 않고 호출하게만 변경
fun example8_2_2(): Unit = runBlocking {
    val result1 = call1_2()
    val result2 = call2_2(result1)

    printWithThread(result2)
}

private suspend fun calculateResult_1(): Int = coroutineScope {
    val num1 = async {
        delay(1_000L)
        10
    }

    val num2 = async {
        delay(1_000L)
        20
    }

    num1.await() + num2.await()
}

// 코루틴 안에서 delay 를 만나면 건너뛰어 다음 코루틴이 실행되지만,(Lec03.kt - example3_1_1 참고)
// coroutineScope 에 의해 만들어진 코루틴이 먼저 호출됨(종료될때까지 다음 코드로 넘어가지 않음)
fun example8_3(): Unit = runBlocking {
    printWithThread("START")
    printWithThread(calculateResult_1())
    printWithThread("END")
}

private suspend fun calculateResult_2(): Int = withContext(Dispatchers.Default) {
    val num1 = async {
        delay(1_000L)
        10
    }

    val num2 = async {
        delay(1_000L)
        20
    }

    num1.await() + num2.await()
}

// 코루틴 안에서 delay 를 만나면 건너뛰어 다음 코루틴이 실행되지만,(Lec03.kt - example3_1_1 참고)
// withContext 에 의해 만들어진 코루틴이 먼저 호출됨(종료될때까지 다음 코드로 넘어가지 않음)
// 추가적으로 withContext 는 컨텍스트에 변화를 줌
fun example8_4(): Unit = runBlocking {
    printWithThread("START")
    printWithThread(calculateResult_1())
    printWithThread("END")
}

// withTimeout - 주어진 시간 내에 코루틴이 완료되지 않으면 예외 발생
fun example8_5_1(): Unit = runBlocking {
    val result: Int = withTimeout(1_000L) {
        delay(1_500L)
        10 + 20
    }
    printWithThread(result)
}

// withTimeoutOrNull - 주어진 시간 내에 코루틴이 완료되지 않으면 null 반환
fun example8_5_2(): Unit = runBlocking {
    val result: Int? = withTimeoutOrNull(1_000L) {
        delay(1_500L)
        10 + 20
    }
    printWithThread(result.toString())
}