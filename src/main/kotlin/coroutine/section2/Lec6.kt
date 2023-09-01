package coroutine.section2

import coroutine.printWithThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Structured Concurrency
 *
 * * Life Cycle 에서 Completing 이 존재하는 이유
 *     * 부모 코루틴 입장에선 자식 코루틴이 몇 개가 존재할 수 있고,
 *     자신의 코드가 종료되어도 자식 코루틴의 코드가 종료될때까지 기다려야 하기 때문
 */

// 자식 코루틴에서 예외가 부모로 전파되면 다른 자식 코루틴에게 취소 요청을 보낸다
fun example6_1_1(): Unit = runBlocking {
    launch {
        delay(600L)
        printWithThread("A")
    }

    launch {
        delay(500L)
        throw IllegalArgumentException("코루틴 실패!")
    }
}