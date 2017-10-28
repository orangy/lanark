package ksdl.system

import kotlinx.cinterop.*
import ksdl.geometry.*
import sdl2.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) {
    val size: KSize

    init {
        size = memScoped {
            val width = alloc<IntVar>()
            val height = alloc<IntVar>()
            SDL_QueryTexture(texturePtr, null, null, width.ptr, height.ptr)
            KSize(width.value, height.value)
        }
        logger.system("Created $this")
    }

    fun release() {
        SDL_DestroyTexture(texturePtr)
        logger.system("Released $this")
    }

    override fun toString() = "Texture ${texturePtr.rawValue}"
}