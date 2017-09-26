package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KCursor(val cursorPtr: CPointer<SDL_Cursor>) {
    init {
        logger.system("Created cursor ${cursorPtr.rawValue}")
    }

    fun destroy() {
        SDL_FreeCursor(cursorPtr)
        logger.system("Destroyed cursor ${cursorPtr.rawValue}")
    }
}