package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KCursor(val cursorPtr: CPointer<SDL_Cursor>) {
    init {
        logger.system("Created $this")
    }

    fun release() {
        SDL_FreeCursor(cursorPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Cursor ${cursorPtr.rawValue}"

}