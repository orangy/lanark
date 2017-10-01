package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

interface KTaskExecutor {
    fun quit()
    fun submitSelf()
    fun submit(task: () -> Unit)
    fun run()
}

class KTaskExecutorIterative() : KTaskExecutor {
    private var quit = false

    private var queue = ArrayList<(() -> Unit)?>(2).apply {
        add(null)
        add(null)
    }

    private var queueHead = 0
    private var queueTail = 0
    private var currentTask: (() -> Unit)? = null

    val beforeIteration = KEventSource<Unit>("BeforeIteration")
    val afterIteration = KEventSource<Unit>("AfterIteration")

    override fun quit() {
        quit = true
    }

    override fun submitSelf() {
        val task = currentTask
        if (task == null)
            logger.error("submitSelf should be called from within executing task only")
        else
            submit(task)
    }

    override fun submit(task: () -> Unit) {
        if (queueHead == queueTail + 1) {
            // queue is filled, expand it (improve algorithm by adding more)
            queue.add(null)
            for (index in queue.lastIndex - 1 downTo queueHead) {
                queue[index + 1] = queue[index]
            }
            queueHead++
            logger.system("Expanded queue: ${queue.size} ")
        } else if (queueHead == 0 && queueTail == queue.lastIndex) {
            queue.add(null)
            logger.system("Expanded queue: ${queue.size} ")
        }

        if (queueTail == queue.lastIndex) {
            queue[queueTail] = task
            queueTail = 0
        } else {
            queue[queueTail++] = task
        }
        //logger.system("Submitted task: ${queue.size} [$queueHead, $queueTail]")
    }

    private fun peek(): (() -> Unit)? {
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
        logger.system("Running event loop")
        while (!quit) {
            beforeIteration()
            runIteration()
            afterIteration()
        }
        logger.system("Stopped event loop")
    }

    private fun runIteration() {
        while (true) {
            val taskToExecute = peek() ?: break
            currentTask = taskToExecute
            taskToExecute()
            currentTask = null
        }
    }

    private fun beforeIteration() {
        beforeIteration.raise(Unit)
    }

    private fun afterIteration() {
        afterIteration.raise(Unit)
    }
}

