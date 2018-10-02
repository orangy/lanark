package org.lanark.system

import kotlinx.coroutines.*

actual fun coroutineLoop(body: suspend CoroutineScope.() -> Unit) {}