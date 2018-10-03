package org.lanark.system

import kotlinx.coroutines.*

actual fun coroutineLoop(body: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(block = body)
}