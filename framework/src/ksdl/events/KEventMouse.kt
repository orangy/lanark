package ksdl.events

import ksdl.geometry.*
import ksdl.system.*
import sdl2.*

abstract class KEventMouse(sdlEventType: SDL_EventType) : KEvent(sdlEventType) {
    companion object {
        fun createEvent(sdlEvent: SDL_Event) = when (sdlEvent.type) {
            SDL_MOUSEBUTTONDOWN -> KEventMouseDown(sdlEvent.button)
            SDL_MOUSEBUTTONUP -> KEventMouseUp(sdlEvent.button)
            SDL_MOUSEMOTION -> KEventMouseMotion(sdlEvent.motion)
            SDL_MOUSEWHEEL -> KEventMouseWheel(sdlEvent.wheel)
            else -> throw KPlatformException("KEventKey.createEvent was called with wrong type of SDL_Event")
        }
    }
}

abstract class KEventMouseButton(val sdlEvent: SDL_MouseButtonEvent) : KEventMouse(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    val clicks get() = sdlEvent.clicks
    val button get() = KMouseButton.fromValue(sdlEvent.button.toInt())
    val state get() = KButtonState.fromValue(sdlEvent.state.toInt())
    val x get() = sdlEvent.x
    val y get() = sdlEvent.y

    val position get() = KPoint(x, y)

    override fun toString() = "#${sdlEvent.windowID} ${super.toString()} $button${if (touch) "[T]" else ""} Button $state ($x, $y)"
}

class KEventMouseDown(sdlEvent: SDL_MouseButtonEvent) : KEventMouseButton(sdlEvent)
class KEventMouseUp(sdlEvent: SDL_MouseButtonEvent) : KEventMouseButton(sdlEvent)

class KEventMouseMotion(val sdlEvent: SDL_MouseMotionEvent) : KEventMouse(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    val state get() = KButtonState.fromValue(sdlEvent.state)
    val x get() = sdlEvent.x
    val y get() = sdlEvent.y
    val deltaX get() = sdlEvent.xrel
    val deltaY get() = sdlEvent.yrel

    val position get() = KPoint(x, y)

    override fun toString() = "#${sdlEvent.windowID} ${super.toString()} ${if (touch) "[T]" else ""} $state ($x, $y) by ($deltaX, $deltaY)"

}

class KEventMouseWheel(val sdlEvent: SDL_MouseWheelEvent) : KEventMouse(sdlEvent.type) {
    val window get() = KPlatform.findWindow(sdlEvent.windowID)
    val timestamp get() = sdlEvent.timestamp
    val touch get() = sdlEvent.which == SDL_TOUCH_MOUSEID
    val scrollX get() = sdlEvent.x
    val scrollY get() = sdlEvent.y
    val direction get() = sdlEvent.direction

    override fun toString() = "#${sdlEvent.windowID} ${super.toString()} ${if (touch) "[T]" else ""} ($scrollX, $scrollY)"
}

enum class KMouseButton {
    Left, Middle, Right, X1, X2;

    companion object {
        fun fromValue(value: Int) = when (value) {
            SDL_BUTTON_LEFT -> Left
            SDL_BUTTON_RIGHT -> Right
            SDL_BUTTON_MIDDLE -> Middle
            SDL_BUTTON_X1 -> X1
            SDL_BUTTON_X2 -> X2
            else -> throw KPlatformException("MouseButton.fromValue was called with unknown value $value")
        }
    }
}
