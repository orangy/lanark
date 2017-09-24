package kdsl

import ksdl.*
import sdl2.*

class KEventSource<T>(val type: SDL_EventType) {
    private val handlers = mutableListOf<(T) -> Unit>()

    fun subscribe(handler: (T) -> Unit) {
        handlers.add(handler)
    }

    fun raise(event: T) {
        handlers.forEach {
            try {
                it(event)
            } catch (e: Throwable) {
                logger.error("Error handling event $type: $e")
            }
        }
    }
}