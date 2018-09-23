package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import sdl2.*

actual abstract class EventApp(private val sdlEvent: SDL_CommonEvent) : Event(sdlEvent.timestamp.toULong()) {
    companion object {
        fun createEvent(sdlEvent: SDL_Event, engine: Engine): EventApp {
            val type = sdlEvent.type
            return when (type) {
                SDL_QUIT -> EventAppQuit(sdlEvent.common)
                SDL_APP_TERMINATING -> EventAppTerminating(sdlEvent.common)
                SDL_APP_LOWMEMORY -> EventAppLowMemory(sdlEvent.common)
                SDL_APP_DIDENTERBACKGROUND -> EventAppEnteredBackground(sdlEvent.common)
                SDL_APP_DIDENTERFOREGROUND -> EventAppEnteredForeground(sdlEvent.common)
                SDL_APP_WILLENTERBACKGROUND -> EventAppEnteringBackground(sdlEvent.common)
                SDL_APP_WILLENTERFOREGROUND -> EventAppEnteringForeground(sdlEvent.common)
                else -> throw PlatformException("EventApp.createEvent was called with unknown type of SDL_Event")
            }
        }
    }

    override fun toString() = "${Events.eventNames[sdlEvent.type]}"
}

actual class EventAppQuit(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppTerminating(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppLowMemory(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppEnteredBackground(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppEnteredForeground(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppEnteringBackground(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
actual class EventAppEnteringForeground(sdlEvent: SDL_CommonEvent) : EventApp(sdlEvent)
