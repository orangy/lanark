package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.system.*

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

    override fun run() {
        engine.logger.system("Running $this")
        running = true
        coroutineLoop {
            while (running) {
                before.raise(Unit)
                if (!running) {
                    coroutineContext.cancel()
                    break
                }

                val toLaunch = scheduled
                scheduled = mutableListOf()

                toLaunch.forEach {
                    launch(block = it)
                }

                yield()

                if (!running) {
                    coroutineContext.cancel()
                    break
                }
                after.raise(Unit)
            }
        }
        engine.logger.system("Stopped $this")
    }
}
