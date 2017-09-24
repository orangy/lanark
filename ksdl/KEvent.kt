package ksdl

import sdl2.*

abstract class KEvent(val eventType: SDL_EventType) {
    override fun toString() = "${KConstantNames.events[eventType]}"
}

