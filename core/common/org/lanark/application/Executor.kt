package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.system.*

interface Executor {
    fun submit(task: suspend CoroutineScope.() -> Unit)

    suspend fun run()
    val before: Signal<Unit>
    val after: Signal<Unit>
    fun stop()
}

expect suspend fun nextTick() : Double
