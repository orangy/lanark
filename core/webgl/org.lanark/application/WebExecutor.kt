package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import kotlin.browser.*

class WebExecutorCoroutines(val engine: Engine) : Executor, CoroutineScope {
    var running = false
        private set

    override val coroutineContext = Job()

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
        engine.logger.system("Running $this")
        running = true
        while (running) {
            before.raise(Unit)
            if (!running) {
                coroutineContext.cancel()
                break
            }

            val toLaunch = scheduled
            scheduled = mutableListOf()

            toLaunch.forEach {
                it()
            }

            val dt = window.awaitAnimationFrame()

            if (!running) {
                coroutineContext.cancel()
                break
            }
            after.raise(Unit)
        }
    }

    private fun loopCompleted(throwable: Throwable?) {
        throwable?.let {
            engine.logger.error(it.message ?: "<No Message>")
            engine.logger.error(it.asDynamic().stack)
        }
    }

    override fun toString(): String = "Executor(WebGL)"

}