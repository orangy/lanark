package org.lanark.events

import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import sdl2.*

actual abstract class EventKey(actual val frame: Frame, private val sdlEvent: SDL_KeyboardEvent) :
    Event(sdlEvent.timestamp.toULong()) {
    val state: EventButtonState get() = EventButtonState.fromValue(sdlEvent.state)
    val repeat: Boolean get() = sdlEvent.repeat != 0.toUByte()
    actual val keyCode: Int get() = sdlEvent.keysym.sym
    actual val scanCode: UInt get() = sdlEvent.keysym.scancode
    val modifiers: Uint16 get() = sdlEvent.keysym.mod

    companion object {
        fun createEvent(sdlEvent: SDL_Event, engine: Engine): EventKey {
            val frame = engine.getFrame(sdlEvent.key.windowID)
            return when (sdlEvent.type) {
                SDL_KEYDOWN -> EventKeyDown(frame, sdlEvent.key)
                SDL_KEYUP -> EventKeyUp(frame, sdlEvent.key)
                else -> throw PlatformException("EventKey.createEvent was called with unknown type of SDL_Event")
            }
        }
    }

    override fun toString() =
        "#${sdlEvent.windowID} ${Events.eventNames[sdlEvent.type]} '${getScanCodeName(scanCode)}' $state ${if (repeat) "Repeated" else ""}"

    private fun getScanCodeName(scanCode: SDL_Scancode) =
        SDL_GetScancodeName(scanCode)?.toKString() ?: "Unknown"
}

actual class EventKeyDown(frame: Frame, sdlEvent: SDL_KeyboardEvent) : EventKey(frame, sdlEvent)
actual class EventKeyUp(frame: Frame, sdlEvent: SDL_KeyboardEvent) : EventKey(frame, sdlEvent)

actual enum class EventButtonState {
    Pressed,
    Released;

    companion object {
        fun fromValue(value: UByte) = when (value.toInt()) {
            SDL_PRESSED -> Pressed
            SDL_RELEASED -> Released
            else -> throw PlatformException("Unknown key state: $value")
        }
    }
}

