package org.lanark.system

import kotlinx.coroutines.*

expect fun coroutineLoop(body: suspend CoroutineScope.()->Unit) 