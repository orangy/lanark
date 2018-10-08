package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import kotlin.coroutines.*

class ExecutorCoroutines(val engine: Engine) : Executor {
    var running = false
        private set

    override val before = Signal<Unit>("BeforeIteration")
    override val after = Signal<Unit>("AfterIteration")

    private var scheduled = mutableListOf<suspend CoroutineScope.() -> Unit>()

    override fun submit(task: suspend CoroutineScope.() -> Unit) {
        scheduled.add(task)
    }

    override fun stop() {
        running = false
    }

    override suspend fun run() {
        val scope = CoroutineScope(kotlin.coroutines.coroutineContext)
        engine.logger.system("Running $this in $scope")
        running = true
        while (running) {
            before.raise(Unit)
            if (!running) {
                coroutineContext.cancel()
                break
            }

            val launching = scheduled
            scheduled = mutableListOf()

            launching.forEach {
                scope.launch {  it() }
            }
            
            yield()
            
            if (!running) {
                coroutineContext.cancel()
                break
            }
            after.raise(Unit) // vsync
        }
        engine.logger.system("Stopped $this")
    }

    override fun toString(): String = "Executor(Coroutines)"
}
