package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.system.*
import sdl2.*

class KCursor(val cursorPtr: CPointer<SDL_Cursor>) : KManaged {
    init {
        logger.system("Created $this")
    }

    override fun release() {
        SDL_FreeCursor(cursorPtr)
        logger.system("Released $this")
    }

    override fun toString() = "Cursor ${cursorPtr.rawValue}"

    companion object {
        fun create(surface: KSurface, hotX: Int, hotY: Int): KCursor {
            val cursor = SDL_CreateColorCursor(surface.surfacePtr, hotX, hotY).checkSDLError("SDL_CreateColorCursor")
            return KCursor(cursor)
        }

        fun create(systemCursor: SDL_SystemCursor): KCursor {
            val cursor = SDL_CreateSystemCursor(systemCursor).checkSDLError("SDL_CreateSystemCursor")
            return KCursor(cursor)
        }
    }
}