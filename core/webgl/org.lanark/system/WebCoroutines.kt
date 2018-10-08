package org.lanark.system

import kotlinx.coroutines.*

actual fun coroutineLoop(body: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(CoroutineExceptionHandler { coroutineContext, e ->
        println("coroutineLoop: $e")
    }, block = body)
}