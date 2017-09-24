package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) {
    init {
        logger.trace("Created texture ${texturePtr.rawValue}")
    }

    fun destroy() {
        SDL_DestroyTexture(texturePtr)
        logger.trace("Destroyed texture ${texturePtr.rawValue}")
    }
}