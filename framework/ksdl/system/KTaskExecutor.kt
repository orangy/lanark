package ksdl.system

interface KTaskExecutor {
    fun submit(task: () -> Unit)

    fun run()
    fun stop()
}

class KTaskExecutorIterative() : KTaskExecutor {
    var running = false
        private set

    private var queue = ArrayList<(() -> Unit)?>(2).apply {
        add(null)
        add(null)
    }

    private var queueHead = 0
    private var queueTail = 0

    val beforeIteration = KEventSource<Unit>("BeforeIteration")
    val afterIteration = KEventSource<Unit>("AfterIteration")

    override fun stop() {
        running = false
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
        logger.system("Running $this")
        running = true
        while (running) {
            beforeIteration()
            runIteration()
            afterIteration()
        }
        logger.system("Stopped $this")
    }

    private fun runIteration() {
        while (true) {
            val taskToExecute = peek() ?: break
            taskToExecute()
        }
    }

    private fun beforeIteration() {
        beforeIteration.raise(Unit)
    }

    private fun afterIteration() {
        afterIteration.raise(Unit)
    }

    override fun toString(): String {
        return "TaskExecutor(Iterative)"
    }
}

