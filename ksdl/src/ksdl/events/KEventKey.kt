package ksdl.events

import sdl2.*
import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.system.*

abstract class KEventKey(protected val sdlEvent: SDL_KeyboardEvent) : KEvent(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp
    val state get() = KButtonState.fromValue(sdlEvent.state.toInt())
    val repeat get() = sdlEvent.repeat.toInt() != 0
    val keyCode get() = sdlEvent.keysym.sym
    val scanCode get() = sdlEvent.keysym.scancode
    val modifiers get() = sdlEvent.keysym.mod

    companion object {
        fun createEvent(sdlEvent: SDL_Event) = when (sdlEvent.type) {
            SDL_KEYDOWN -> KEventKeyDown(sdlEvent.key)
            SDL_KEYUP -> KEventKeyUp(sdlEvent.key)
            else -> throw KPlatformException("KEventKey.createEvent was called with wrong type of SDL_Event")
        }
    }

    override fun toString() = "#${sdlEvent.windowID} ${super.toString()} '${getScanCodeName(scanCode)}' $state ${if (repeat) "Repeated" else ""}"

    private fun getScanCodeName(scanCode: SDL_Scancode) =
            SDL_GetScancodeName(scanCode)?.toKString() ?: "Unknown"
}

class KEventKeyDown(sdlEvent: SDL_KeyboardEvent) : KEventKey(sdlEvent)
class KEventKeyUp(sdlEvent: SDL_KeyboardEvent) : KEventKey(sdlEvent)

enum class KButtonState {
    Pressed, Released;

    companion object {
        fun fromValue(value: Int) = when (value) {
            SDL_PRESSED -> Pressed
            SDL_RELEASED -> Released
            else -> throw KPlatformException("Unknown key state: $value")
        }
    }
}

