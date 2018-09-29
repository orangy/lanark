package org.lanark.application

import cnames.structs.SDL_Window
import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*
import sdl2.*

actual class Frame(actual val engine: Engine, internal val windowPtr: CPointer<SDL_Window>) : ResourceOwner, Managed {
    val id: UInt get() = SDL_GetWindowID(windowPtr)

    internal val rendererPtr = SDL_CreateRenderer(
        windowPtr,
        -1,
        SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC
    ).sdlError("SDL_CreateRenderer")

    private val resizeHandler: (EventWindow) -> Unit = {
        if (it is EventWindowResized && it.frame == this) {
            val drawableSize = canvasSize // need this for HIDPI
            SDL_RenderSetLogicalSize(rendererPtr, drawableSize.width, drawableSize.height)
        }
    }

    init {
        engine.events.window.subscribe(resizeHandler)
    }

    override fun release() {
        engine.events.window.unsubscribe(resizeHandler)
        engine.unregisterFrame(id, this)
        val captureId = id
        SDL_DestroyRenderer(rendererPtr)
        engine.logger.system("Released renderer $rendererPtr")
        SDL_DestroyWindow(windowPtr)
        engine.logger.system("Released window #$captureId ${windowPtr.rawValue}")
    }

    actual fun setBordered(enable: Boolean) {
        SDL_SetWindowBordered(windowPtr, enable.toSDLBoolean())
    }

    actual fun setResizable(enable: Boolean) {
        SDL_SetWindowResizable(windowPtr, enable.toSDLBoolean())
    }

    actual fun setWindowMode(mode: FrameMode) {
        SDL_SetWindowFullscreen(
            windowPtr, when (mode) {
                FrameMode.Windowed -> 0u
                FrameMode.FullScreen -> SDL_WINDOW_FULLSCREEN
                FrameMode.FullScreenDesktop -> SDL_WINDOW_FULLSCREEN_DESKTOP
            }
        )
    }

    actual fun setIcon(icon: Canvas) {
        SDL_SetWindowIcon(windowPtr, icon.surfacePtr)
    }

    actual val size: Size
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowSize(windowPtr, w.ptr, h.ptr)
            Size(w.value, h.value)
        }

    actual val canvasSize: Size
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GL_GetDrawableSize(windowPtr, w.ptr, h.ptr)
            Size(w.value, h.value)
        }

    actual var minimumSize: Size
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowMinimumSize(windowPtr, w.ptr, h.ptr)
            Size(w.value, h.value)
        }
        set(value) {
            SDL_SetWindowMinimumSize(windowPtr, value.width, value.height)
        }

    actual var maximumSize: Size
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowMaximumSize(windowPtr, w.ptr, h.ptr)
            Size(w.value, h.value)
        }
        set(value) {
            SDL_SetWindowMaximumSize(windowPtr, value.width, value.height)
        }

    actual var brightness: Float
        get() = SDL_GetWindowBrightness(windowPtr)
        set(value) = SDL_SetWindowBrightness(windowPtr, value).sdlError("SDL_SetWindowBrightness")

    actual var title: String
        get() = SDL_GetWindowTitle(windowPtr).sdlError("SDL_GetWindowTitle").toKString()
        set(value) = SDL_SetWindowTitle(windowPtr, value)


    actual val borders: Margins
        get() = memScoped {
            val top = alloc<IntVar>()
            val left = alloc<IntVar>()
            val bottom = alloc<IntVar>()
            val right = alloc<IntVar>()
            SDL_GetWindowBordersSize(
                windowPtr,
                top.ptr,
                left.ptr,
                bottom.ptr,
                right.ptr
            ).sdlError("SDL_GetWindowBordersSize")
            Margins(top.value, left.value, bottom.value, right.value)
        }


    actual fun messageBox(title: String, message: String, icon: MessageBoxIcon) {
        engine.messageBox(title, message, icon, this)
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

    actual fun drawLine(from: Point, to: Point) {
        SDL_RenderDrawLine(rendererPtr, from.x, from.y, to.x, to.y).sdlError("SDL_RenderDrawLine")
    }

    actual fun present() {
        SDL_RenderPresent(rendererPtr)
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


    actual companion object {
        actual val UndefinedPosition = SDL_WINDOWPOS_UNDEFINED.toInt()

        actual val CreateShown: UInt = SDL_WINDOW_SHOWN
        actual val CreateResizable: UInt = SDL_WINDOW_RESIZABLE
        actual val CreateFullscreen: UInt = SDL_WINDOW_FULLSCREEN
        actual val CreateHiDPI: UInt = SDL_WINDOW_ALLOW_HIGHDPI
        actual val CreateOpenGL: UInt = SDL_WINDOW_OPENGL
    }

    override fun toString() = "Window #$id ${windowPtr.rawValue}"
}
