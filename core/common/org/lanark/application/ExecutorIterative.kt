package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.system.*

class ExecutorIterative(val engine: Engine) : Executor {
    var running = false
        private set

    private var queue = ArrayList<(suspend CoroutineScope.() -> Unit)?>(2).apply {
        add(null)
        add(null)
    }

    private var queueHead = 0
    private var queueTail = 0

    override val before = Signal<Unit>("BeforeIteration")
    override val after = Signal<Unit>("AfterIteration")

    override fun stop() {
        running = false
    }

    override fun submit(task: suspend CoroutineScope.() -> Unit) {
        if (queueHead == queueTail + 1) {
            // queue is filled, expand it (improve algorithm by adding more)
            queue.add(null)
            for (index in queue.lastIndex - 1 downTo queueHead) {
                queue[index + 1] = queue[index]
            }
            queueHead++
            engine.logger.system("Expanded queue: ${queue.size} ")
        } else if (queueHead == 0 && queueTail == queue.lastIndex) {
            queue.add(null)
            engine.logger.system("Expanded queue: ${queue.size} ")
        }

        if (queueTail == queue.lastIndex) {
            queue[queueTail] = task
            queueTail = 0
        } else {
            queue[queueTail++] = task
        }
        //logger.system("Submitted task: ${queue.size} [$queueHead, $queueTail]")
    }

    private fun peek(): (suspend CoroutineScope.() -> Unit)? {
        if (queueHead == queueTail) return null
        val task = queue[queueHead]
        queue[queueHead] = null
        if (queueHead == queue.lastIndex)
            queueHead = 0
        else
            queueHead++
        //logger.system("Peeked task: ${queue.size} [$queueHead, $queueTail]")
        return task
    }

    override fun run() {
        engine.logger.system("Running $this")
        running = true
        while (running) {
            beforeIteration()
            if (!running)
                break
            runIteration()
            if (!running)
                break
            afterIteration()
        }
        engine.logger.system("Stopped $this")
    }

    private fun runIteration() {
        while (running) {
            val taskToExecute = peek() ?: break
            coroutineLoop {
                taskToExecute()
            }
        }
    }

    private fun beforeIteration() {
        before.raise(Unit)
    }

    private fun afterIteration() {
        after.raise(Unit)
    }

    override fun toString(): String = "Executor(Iterative)"
}