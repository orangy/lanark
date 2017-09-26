package ksdl.system

import kotlinx.cinterop.*
import ksdl.system.*
import sdl2.*

class KEventLoop() {
    private var quit = false

    private var queue = ArrayList<(() -> Unit)?>(2).apply {
        add(null)
        add(null)
    }

    private var queueHead = 0
    private var queueTail = 0

    var currentTask: (() -> Unit)? = null

    val windowEvents = KEventSource<KEventWindow>("Window")
    val appEvents = KEventSource<KEventApp>("App")
    val keyEvents = KEventSource<KEventKey>("Key")
    val mouseEvents = KEventSource<KEventMouse>("Mouse")

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

    fun peek(): (() -> Unit)? {
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

    fun run() {
        logger.system("Running event loop")
        while (!quit) {
            while (true) {
                memScoped {
                    val event = alloc<SDL_Event>()
                    while (SDL_PollEvent(event.ptr) == 1) {
                        processEvent(event)
                        if (quit)
                            break
                    }
                }

                if (quit)
                    break

                currentTask = peek()
                (currentTask ?: break)()
                currentTask = null
            }
        }
        logger.system("Stopped event loop")
    }

    private fun processEvent(event: SDL_Event) {
        val eventName = KConstantNames.events[event.type]
        when (event.type) {
            SDL_QUIT -> {
                quit = true
                logger.system("Event: SDL_QUIT")
                return
            }
            SDL_APP_TERMINATING, SDL_APP_LOWMEMORY, SDL_APP_DIDENTERBACKGROUND,
            SDL_APP_DIDENTERFOREGROUND, SDL_APP_WILLENTERBACKGROUND, SDL_APP_WILLENTERFOREGROUND -> {
                val kevent = KEventApp.createEvent(event)
                logger.system("Event: $kevent")
                appEvents.raise(kevent)
            }
            SDL_WINDOWEVENT -> {
                val kevent = KEventWindow.createEvent(event)
                logger.system("Event: $kevent")
                windowEvents.raise(kevent)
            }
            SDL_KEYUP, SDL_KEYDOWN -> {
                val kevent = KEventKey.createEvent(event)
                logger.system("Event: $kevent")
                keyEvents.raise(kevent)
            }
            SDL_MOUSEBUTTONDOWN, SDL_MOUSEBUTTONUP, SDL_MOUSEMOTION, SDL_MOUSEWHEEL -> {
                val kevent = KEventMouse.createEvent(event)
                logger.system("Event: $kevent")
                mouseEvents.raise(kevent)

            }
            else -> {
                if (eventName == null)
                    logger.system("Unknown event eventType: ${event.type}")
                else
                    logger.system("Event: $eventName")
            }
        }
    }
}

