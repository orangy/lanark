package org.lanark.events

import kotlinx.cinterop.*
import org.lanark.application.*
import sdl2.*

actual fun getScanCodeName(scanCode: UInt): String? {
    return SDL_GetScancodeName(scanCode)?.toKString()
}

fun createKeyEvent(sdlEvent: SDL_Event, engine: Engine): EventKey {
    val timestamp = sdlEvent.common.timestamp.toULong()
    val keyEvent = sdlEvent.key
    val frame = engine.getFrame(keyEvent.windowID)
    return when (sdlEvent.type) {
        SDL_KEYDOWN -> EventKeyDown(
            timestamp,
            frame,
            keyEvent.keysym.sym,
            keyEvent.keysym.scancode,
            keyEvent.repeat != 0.toUByte()
        )
        SDL_KEYUP -> EventKeyUp(timestamp, frame, keyEvent.keysym.sym, keyEvent.keysym.scancode)
        else -> throw EngineException("EventKey.createEvent was called with unknown type of SDL_Event")
    }
}
