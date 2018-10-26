package org.lanark.events

import org.lanark.application.*
import sdl2.*

fun createMouseEvent(sdlEvent: SDL_Event, engine: Engine): EventMouse {
    val timestamp = sdlEvent.common.timestamp.toULong()
    return when (sdlEvent.type) {
        SDL_MOUSEBUTTONDOWN -> {
            val buttonEvent = sdlEvent.button
            EventMouseButtonDown(
                timestamp,
                engine.getFrame(buttonEvent.windowID),
                mouseButtonFromValue(buttonEvent.button),
                buttonEvent.x,
                buttonEvent.y,
                buttonEvent.clicks.toUInt()
            )
        }
        SDL_MOUSEBUTTONUP -> {
            val buttonEvent = sdlEvent.button
            EventMouseButtonUp(
                timestamp,
                engine.getFrame(buttonEvent.windowID),
                mouseButtonFromValue(buttonEvent.button),
                buttonEvent.x,
                buttonEvent.y,
                buttonEvent.clicks.toUInt()
            )
        }
        SDL_MOUSEMOTION -> {
            val motionEvent = sdlEvent.motion
            EventMouseMotion(
                timestamp,
                engine.getFrame(motionEvent.windowID),
                motionEvent.x,
                motionEvent.y,
                motionEvent.xrel,
                motionEvent.yrel
            )
        }
        SDL_MOUSEWHEEL -> {
            val wheelEvent = sdlEvent.wheel
            EventMouseScroll(
                timestamp,
                engine.getFrame(wheelEvent.windowID),
                wheelEvent.x,
                wheelEvent.y
            )
        }
        else -> throw EngineException("EventMouse.createEvent was called with unknown type of SDL_Event")
    }
}

fun mouseButtonFromValue(value: UByte) = when (value.toInt()) {
    SDL_BUTTON_LEFT -> MouseButton.Left
    SDL_BUTTON_RIGHT -> MouseButton.Right
    SDL_BUTTON_MIDDLE -> MouseButton.Middle
    SDL_BUTTON_X1 -> MouseButton.X1
    SDL_BUTTON_X2 -> MouseButton.X2
    else -> throw EngineException("MouseButton.fromValue was called with unknown value $value")
}

fun buttonStateFromValue(value: UByte) = when (value.toInt()) {
    SDL_PRESSED -> EventButtonState.Pressed
    SDL_RELEASED -> EventButtonState.Released
    else -> throw EngineException("Unknown key state: $value")
}