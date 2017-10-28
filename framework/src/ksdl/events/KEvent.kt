package ksdl.events

import sdl2.*

abstract class KEvent(val eventType: SDL_EventType) {
    override fun toString() = "${KEventNames.events[eventType]}"
}

