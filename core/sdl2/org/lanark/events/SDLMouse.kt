package org.lanark.events

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import sdl2.*

actual abstract class EventMouse(actual val frame: Frame, private val sdlEventType: UInt, timestamp: UInt) : Event(timestamp.toULong()) {
    companion object {
        fun createEvent(sdlEvent: SDL_Event, engine: Engine) = when (sdlEvent.type) {
            SDL_MOUSEBUTTONDOWN -> EventMouseButtonDown(engine.getFrame(sdlEvent.button.windowID), sdlEvent.button)
            SDL_MOUSEBUTTONUP -> EventMouseButtonUp(engine.getFrame(sdlEvent.button.windowID), sdlEvent.button)
            SDL_MOUSEMOTION -> EventMouseMotion(engine.getFrame(sdlEvent.motion.windowID), sdlEvent.motion)
            SDL_MOUSEWHEEL -> EventMouseWheel(engine.getFrame(sdlEvent.wheel.windowID), sdlEvent.wheel)
            else -> throw PlatformException("EventMouse.createEvent was called with unknown type of SDL_Event")
        }
    }

    override fun toString(): String = "${Events.eventNames[sdlEventType]}"
}

actual abstract class EventMouseButton(frame: Frame, private val sdlEvent: SDL_MouseButtonEvent) : EventMouse(frame, sdlEvent.type, sdlEvent.timestamp) {
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    actual val clicks: UInt get() = sdlEvent.clicks.toUInt()
    actual val button: MouseButton get() = MouseButton.fromValue(sdlEvent.button)
    actual val state: EventButtonState get() = EventButtonState.fromValue(sdlEvent.state)
    actual val x get() = sdlEvent.x
    actual val y get() = sdlEvent.y

    override fun toString() =
        "#${sdlEvent.windowID} ${super.toString()} $button${if (touch) "[T]" else ""} Button $state ($x, $y)"
}

actual class EventMouseButtonDown(frame: Frame, sdlEvent: SDL_MouseButtonEvent) : EventMouseButton(frame, sdlEvent)
actual class EventMouseButtonUp(frame: Frame, sdlEvent: SDL_MouseButtonEvent) : EventMouseButton(frame, sdlEvent)

actual class EventMouseMotion(frame: Frame, private val sdlEvent: SDL_MouseMotionEvent) : EventMouse(frame, sdlEvent.type, sdlEvent.timestamp) {
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    val state: EventButtonState get() = EventButtonState.fromValue(sdlEvent.state.toUByte())
    actual val x get() = sdlEvent.x
    actual val y get() = sdlEvent.y
    actual val deltaX get() = sdlEvent.xrel
    actual val deltaY get() = sdlEvent.yrel

    override fun toString() =
        "#${sdlEvent.windowID} ${super.toString()} ${if (touch) "[T]" else ""} $state ($x, $y) by ($deltaX, $deltaY)"

}

actual class EventMouseWheel(frame: Frame, private val sdlEvent: SDL_MouseWheelEvent) : EventMouse(frame, sdlEvent.type, sdlEvent.timestamp) {
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    actual val scrollX get() = sdlEvent.x
    actual val scrollY get() = sdlEvent.y
    val direction get() = sdlEvent.direction

    override fun toString() =
        "#${sdlEvent.windowID} ${super.toString()} ${if (touch) "[T]" else ""} ($scrollX, $scrollY)"
}

actual enum class MouseButton {
    Left,
    Middle,
    Right,
    X1,
    X2;

    companion object {
        fun fromValue(value: UByte) = when (value.toInt()) {
            SDL_BUTTON_LEFT -> Left
            SDL_BUTTON_RIGHT -> Right
            SDL_BUTTON_MIDDLE -> Middle
            SDL_BUTTON_X1 -> X1
            SDL_BUTTON_X2 -> X2
            else -> throw PlatformException("MouseButton.fromValue was called with unknown value $value")
        }
    }
}
