package org.lanark.application

import cnames.structs.SDL_Cursor
import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.system.*
import sdl2.*
import sdl2.SDL_SystemCursor.*

actual class Cursor(private val logger: Logger, internal val cursorPtr: CPointer<SDL_Cursor>) : Managed {
    override fun release() {
        SDL_FreeCursor(cursorPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Cursor ${cursorPtr.rawValue}"
}

actual enum class SystemCursor(val handle: SDL_SystemCursor) {
    Arrow(SDL_SYSTEM_CURSOR_ARROW),
    IBeam(SDL_SYSTEM_CURSOR_IBEAM),
    Wait(SDL_SYSTEM_CURSOR_WAIT),
    CrossHair(SDL_SYSTEM_CURSOR_CROSSHAIR),
    WaitArrow(SDL_SYSTEM_CURSOR_WAITARROW),
    SizeNWSE(SDL_SYSTEM_CURSOR_SIZENWSE),
    SizeNESW(SDL_SYSTEM_CURSOR_SIZENESW),
    SizeWE(SDL_SYSTEM_CURSOR_SIZEWE),
    SizeNS(SDL_SYSTEM_CURSOR_SIZENS),
    SizeAll(SDL_SYSTEM_CURSOR_SIZEALL),
    No(SDL_SYSTEM_CURSOR_NO),
    Hand(SDL_SYSTEM_CURSOR_HAND)
}