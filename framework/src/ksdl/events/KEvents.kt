package ksdl.events

import kotlinx.cinterop.*
import ksdl.system.*
import sdl2.*

class KEvents {
    val window = KSignal<KEventWindow>("Window")
    val application = KSignal<KEventApp>("App")
    val keyboard = KSignal<KEventKey>("Key")
    val mouse = KSignal<KEventMouse>("Mouse")

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
                logger.event(kevent.toString())
                application.raise(kevent)
            }
            SDL_WINDOWEVENT -> {
                val kevent = KEventWindow.createEvent(event)
                logger.event(kevent.toString())
                window.raise(kevent)
            }
            SDL_KEYUP, SDL_KEYDOWN -> {
                val kevent = KEventKey.createEvent(event)
                logger.event(kevent.toString())
                keyboard.raise(kevent)
            }
            SDL_MOUSEBUTTONDOWN, SDL_MOUSEBUTTONUP, SDL_MOUSEMOTION, SDL_MOUSEWHEEL -> {
                val kevent = KEventMouse.createEvent(event)
                logger.event(kevent.toString())
                mouse.raise(kevent)
            }
            SDL_FINGERMOTION, SDL_FINGERDOWN, SDL_FINGERUP -> {
                // ignore event and don't log it
            }
            else -> {
                if (eventName == null)
                    logger.event("Unknown event eventType: ${event.type}")
                else
                    logger.event(eventName.toString())
            }
        }
    }

    companion object {
        val logCategory = KLogCategory("Events", "\u001B[0;36m")
    }
}

fun KLogger.event(message: String) = log(KEvents.logCategory, message)
