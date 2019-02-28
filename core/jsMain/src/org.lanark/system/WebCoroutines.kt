package org.lanark.system

import kotlinx.coroutines.*

actual fun coroutineLoop(body: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(CoroutineExceptionHandler { coroutineContext, e ->
        println("coroutineLoop: $e")
        println(e.asDynamic().stack)
    }) {
        try {
            body()
        } catch (e: Throwable) {
            println("coroutineLoop body: $e")
            println(e.asDynamic().stack)
        }
    }
}