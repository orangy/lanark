package ksdl.system

import kotlinx.cinterop.*
import sdl2.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) {
    init {
        logger.system("Created texture ${texturePtr.rawValue}")
    }

    fun destroy() {
        SDL_DestroyTexture(texturePtr)
        logger.system("Destroyed texture ${texturePtr.rawValue}")
    }
}