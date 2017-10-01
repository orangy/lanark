package ksdl.events

import ksdl.system.*
import sdl2.*

abstract class KEvent(val eventType: SDL_EventType) {
    override fun toString() = "${KConstantNames.events[eventType]}"
}

