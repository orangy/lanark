package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KCursor(val cursorPtr: CPointer<SDL_Cursor>) {
    init {
        logger.trace("Created cursor ${cursorPtr.rawValue}")
    }

    fun destroy() {
        SDL_FreeCursor(cursorPtr)
        logger.trace("Destroyed cursor ${cursorPtr.rawValue}")
    }
}