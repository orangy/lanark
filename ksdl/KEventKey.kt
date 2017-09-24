package ksdl

import sdl2.*

abstract class KEventKey(protected val sdlEvent: SDL_KeyboardEvent) : KEvent(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp
    val state get() = KKeyState.fromState(sdlEvent.state)
    val repeat get() = sdlEvent.repeat.toInt() != 0
    val keyCode get() = sdlEvent.keysym.sym
    val scanCode get() = sdlEvent.keysym.scancode
    val modifiers get() = sdlEvent.keysym.mod

    companion object {
        fun createEvent(sdlEvent: SDL_Event) = when (sdlEvent.type) {
            SDL_KEYDOWN -> KEventKeyDown(sdlEvent.key)
            SDL_KEYUP -> KEventKeyUp(sdlEvent.key)
            else -> throw KGraphicsException("KEventKey.createEvent was called with wrong type of SDL_Event")
        }
    }

    override fun toString() = "#${sdlEvent.windowID} ${super.toString()} '${KConstantNames.scanCodes[scanCode]}' $state ${if (repeat) "Repeated" else ""}"
}

class KEventKeyDown(sdlEvent: SDL_KeyboardEvent) : KEventKey(sdlEvent)
class KEventKeyUp(sdlEvent: SDL_KeyboardEvent) : KEventKey(sdlEvent)

enum class KKeyState {
    Pressed, Released;

    companion object {
        fun fromState(state: Uint8) = when (state.toInt()) {
            SDL_PRESSED -> Pressed
            SDL_RELEASED -> Released
            else -> throw KGraphicsException("Unknown key state: $state")
        }
    }
}

