package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.geometry.*
import ksdl.system.*
import sdl2.*

class KTexture(val texturePtr: CPointer<SDL_Texture>) : KManaged {
    val size: KSize = memScoped {
        val width = alloc<IntVar>()
        val height = alloc<IntVar>()
        SDL_QueryTexture(texturePtr, null, null, width.ptr, height.ptr)
        KSize(width.value, height.value)
    }

    init {
        logger.system("Created $this")
    }

    override fun release() {
        SDL_DestroyTexture(texturePtr)
        logger.system("Released $this")
    }

    override fun toString() = "Texture ${texturePtr.rawValue}"
}