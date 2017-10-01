package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KEvents {
    val windowEvents = KEventSource<KEventWindow>("Window")
    val appEvents = KEventSource<KEventApp>("App")
    val keyEvents = KEventSource<KEventKey>("Key")
    val mouseEvents = KEventSource<KEventMouse>("Mouse")

    fun pollEvents() = memScoped {
        val event = alloc<SDL_Event>()
        while (SDL_PollEvent(event.ptr) == 1) {
            processEvent(event)
        }
    }

    private fun processEvent(event: SDL_Event) {
        val eventName = KConstantNames.events[event.type]
        when (event.type) {
            SDL_QUIT,
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