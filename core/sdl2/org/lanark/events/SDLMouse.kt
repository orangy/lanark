package org.lanark.events

import org.lanark.application.*
import sdl2.*

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