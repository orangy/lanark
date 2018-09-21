package ksdl.system

import kotlinx.coroutines.*

class KTaskExecutorCoroutine : KTaskExecutor {
    override val before = KSignal<Unit>("BeforeIteration")
    override val after = KSignal<Unit>("AfterIteration")

    override fun submit(task: () -> Unit) {
    }

    override fun run() {
        runBlocking {
            
        }
    }

    override fun stop() {
        
    }
}