package ksdl

import eventNames
import sdl2.*

abstract class KEvent(val eventType: SDL_EventType) {
    override fun toString() = "${eventNames[eventType]}"
}

