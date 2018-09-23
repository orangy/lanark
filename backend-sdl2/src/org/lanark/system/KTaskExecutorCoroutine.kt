package org.lanark.system

import kotlinx.coroutines.*

class KTaskExecutorCoroutine : KTaskExecutor {
    override val before = Signal<Unit>("BeforeIteration")
    override val after = Signal<Unit>("AfterIteration")

    override fun submit(task: () -> Unit) {
    }

    override fun run() {
        runBlocking {
            
        }
    }

    override fun stop() {
        
    }
}