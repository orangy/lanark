package org.lanark.drawing

import cnames.structs.SDL_Renderer
import kotlinx.cinterop.*
import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import sdl2.*

actual class Renderer(actual val frame: Frame, val rendererPtr: CPointer<SDL_Renderer>) : ResourceOwner, Managed {
    actual var size: Size
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_RenderGetLogicalSize(rendererPtr, w.ptr, h.ptr)
            Size(w.value, h.value)
        }
        set(value) {
            SDL_RenderSetLogicalSize(rendererPtr, value.width, value.height)
            frame.engine.logger.system("Resized $this for window #${frame.id} to $size")
        }

    actual var clip: Rect?
        get() = memScoped {
            val rect = alloc<SDL_Rect>()
            SDL_RenderGetClipRect(rendererPtr, rect.ptr)
            if (rect.w == 0 && rect.h == 0)
                null
            else
                Rect(rect.x, rect.y, rect.w, rect.h)
        }
        set(value) = memScoped {
            if (value == null)
                SDL_RenderSetClipRect(rendererPtr, null)
            else
                SDL_RenderSetClipRect(rendererPtr, SDL_Rect(value))
        }

    init {
        size = frame.size
        frame.engine.logger.system("Created $this for window #${frame.id}")
    }

    actual fun clear(color: Color?) {
        if (color != null)
            color(color)
        SDL_RenderClear(rendererPtr).sdlError("SDL_RenderClear")
    }

    actual fun color(color: Color) {
        SDL_SetRenderDrawColor(
            rendererPtr,
            color.red,
            color.green,
            color.blue,
            color.alpha
        ).sdlError("SDL_SetRenderDrawColor")
    }

    actual fun scale(scale: Float) {
        SDL_RenderSetScale(rendererPtr, scale, scale).sdlError("SDL_RenderSetScale")
    }

    actual fun present() {
        SDL_RenderPresent(rendererPtr)
    }

    override fun release() {
        SDL_DestroyRenderer(rendererPtr)
        frame.engine.logger.system("Released $this")
    }

    actual fun drawLine(from: Point, to: Point) {
        SDL_RenderDrawLine(rendererPtr, from.x, from.y, to.x, to.y).sdlError("SDL_RenderDrawLine")
    }

    override fun toString() = "Renderer ${rendererPtr.rawValue}"
}

