package ksdl.rendering

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.geometry.*
import ksdl.system.*
import sdl2.*

class KRenderer(val window: KWindow, val rendererPtr: CPointer<SDL_Renderer>) : KManaged {
    var size: KSize
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_RenderGetLogicalSize(rendererPtr, w.ptr, h.ptr)
            KSize(w.value, h.value)
        }
        set(value) {
            SDL_RenderSetLogicalSize(rendererPtr, value.width, value.height)
            logger.system("Resized $this for window #${window.id} to $size")
        }

    init {
        size = window.size
        logger.system("Created $this for window #${window.id}")
    }

    fun clear(color: KColor? = null) {
        if (color != null)
            color(color)
        SDL_RenderClear(rendererPtr).checkSDLError("SDL_RenderClear")
    }

    fun color(color: KColor) {
        SDL_SetRenderDrawColor(rendererPtr, color.red.toByte(), color.green.toByte(), color.blue.toByte(), color.alpha.toByte()).checkSDLError("SDL_SetRenderDrawColor")
    }

    fun scale(scale: Float) {
        SDL_RenderSetScale(rendererPtr, scale, scale).checkSDLError("SDL_RenderSetScale")
    }

    fun present() {
        SDL_RenderPresent(rendererPtr)
    }

    override fun release() {
        SDL_DestroyRenderer(rendererPtr)
        logger.system("Released $this")
    }

    fun drawLine(from: KPoint, to: KPoint) {
        SDL_RenderDrawLine(rendererPtr, from.x, from.y, to.x, to.y).checkSDLError("SDL_RenderDrawLine")
    }

    override fun toString() = "Renderer ${rendererPtr.rawValue}"
}