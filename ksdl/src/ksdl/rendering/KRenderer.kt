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

    var clip: KRect?
        get() = memScoped {
            val rect = alloc<SDL_Rect>()
            SDL_RenderGetClipRect(rendererPtr, rect.ptr)
            if (rect.w == 0 && rect.h == 0)
                null
            else
                KRect(rect.x, rect.y, rect.w, rect.h)
        }
        set(value) = memScoped {
            if (value == null)
                SDL_RenderSetClipRect(rendererPtr, null)
            else
                SDL_RenderSetClipRect(rendererPtr, SDL_Rect(value))
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
        SDL_SetRenderDrawColor(rendererPtr, color.red, color.green, color.blue, color.alpha).checkSDLError("SDL_SetRenderDrawColor")
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

inline fun KRenderer.withClip(rectangle: KRect, body: () -> Unit) {
    val old = clip
    try {
        clip = rectangle
        body()
    } finally {
        clip = old
    }
}