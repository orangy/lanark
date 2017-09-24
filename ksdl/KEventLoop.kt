package kdsl

import kotlinx.cinterop.*
import ksdl.*
import sdl2.*

class KEventLoop() {
    private var quit = false

    private var queue = ArrayList<(() -> Unit)?>(2).apply {
        add(null)
        add(null)
    }

    private var queueHead = 0
    private var queueTail = 0

    val windowEvents = KEventSource<SDL_WindowEvent>(SDL_WINDOWEVENT)

    fun submitSelf() {
        val task = currentTask
        if (task == null)
            logger.error("submitSelf should be called from within executing task only")
        else
            submit(task)
    }

    fun submit(task: () -> Unit) {
        if (queueHead == queueTail + 1) {
            // queue is filled, expand it (improve algorithm by adding more)
            queue.add(null)
            for (index in queue.lastIndex - 1 downTo queueHead) {
                queue[index + 1] = queue[index]
            }
            queueHead++
            logger.trace("Expanded queue: ${queue.size} [$queueHead, $queueTail]")
        } else if (queueHead == 0 && queueTail == queue.lastIndex) {
            queue.add(null)
            logger.trace("Expanded queue: ${queue.size} [$queueHead, $queueTail]")
        }

        if (queueTail == queue.lastIndex) {
            queue[queueTail] = task
            queueTail = 0
        } else {
            queue[queueTail++] = task
        }
        logger.trace("Submitted task: ${queue.size} [$queueHead, $queueTail]")
    }

    fun peek(): (() -> Unit)? {
        if (queueHead == queueTail) return null
        val task = queue[queueHead]
        queue[queueHead] = null
        if (queueHead == queue.lastIndex)
            queueHead = 0
        else
            queueHead++
        logger.trace("Peeked task: ${queue.size} [$queueHead, $queueTail]")
        return task
    }

    fun run() {
        logger.trace("Running event loop")
        while (!quit) {
            memScoped {
                val event = alloc<SDL_Event>()
                val hasEvent = SDL_PollEvent(event.ptr) == 1
                if (hasEvent)
                    processEvent(event)
            }
            while (true) {
                currentTask = peek()
                (currentTask ?: break)()
                currentTask = null
            }
        }
        logger.trace("Stopped event loop")
    }

    private fun processEvent(event: SDL_Event) {
        logger.trace("Received event: ${event.type}")
        when (event.type) {
            SDL_QUIT -> {
                quit = true
                return
            }
            SDL_WINDOWEVENT -> windowEvents.raise(event.window)
            else -> logger.trace("Unknown event type: ${event.type}")
        }
    }

    var currentTask: (() -> Unit)? = null
}

