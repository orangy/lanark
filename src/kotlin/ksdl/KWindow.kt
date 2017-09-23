package ksdl

import kotlinx.cinterop.*
import sdl2.*

class KWindow(val windowPtr: CPointer<SDL_Window>) {

    fun destroy() {
        SDL_DestroyWindow(windowPtr)
    }

    fun setBordered(enable: Boolean) {
        SDL_SetWindowBordered(windowPtr, enable.toSDLBoolean())
    }

    fun setWindowMode(mode: Mode) {
        SDL_SetWindowFullscreen(windowPtr, when (mode) {
            Mode.Windowed -> 0
            Mode.FullScreen -> SDL_WINDOW_FULLSCREEN
            Mode.FullScreenDesktop -> SDL_WINDOW_FULLSCREEN_DESKTOP
        })
    }

    fun setIcon(icon: KSurface) {
        SDL_SetWindowIcon(windowPtr, icon.surfacePtr)
    }

    val size: KSize
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowSize(windowPtr, w.ptr, h.ptr)
            KSize(w.value, h.value)
        }

    var minimumSize: KSize
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowMinimumSize(windowPtr, w.ptr, h.ptr)
            KSize(w.value, h.value)
        }
        set(value) {
            SDL_SetWindowMinimumSize(windowPtr, value.width, value.height)
        }

    var maximumSize: KSize
        get() = memScoped {
            val w = alloc<IntVar>()
            val h = alloc<IntVar>()
            SDL_GetWindowMaximumSize(windowPtr, w.ptr, h.ptr)
            KSize(w.value, h.value)
        }
        set(value) {
            SDL_SetWindowMaximumSize(windowPtr, value.width, value.height)
        }

    var brightness: Float
        get() = SDL_GetWindowBrightness(windowPtr)
        set(value) = SDL_SetWindowBrightness(windowPtr, value).checkSDLError("SDL_SetWindowBrightness")

    var title: String
        get() = SDL_GetWindowTitle(windowPtr).checkSDLError("SDL_GetWindowTitle").toKString()
        set(value) = SDL_SetWindowTitle(windowPtr, value)


    val borders: KMargins
        get() = memScoped {
            val top = alloc<IntVar>()
            val left = alloc<IntVar>()
            val bottom = alloc<IntVar>()
            val right = alloc<IntVar>()
            SDL_GetWindowBordersSize(windowPtr, top.ptr, left.ptr, bottom.ptr, right.ptr).checkSDLError("SDL_GetWindowBordersSize")
            KMargins(top.value, left.value, bottom.value, right.value)
        }


    fun renderer(rendererFlags: Int = SDL_RENDERER_ACCELERATED or SDL_RENDERER_PRESENTVSYNC): KRenderer {
        val renderer = SDL_CreateRenderer(windowPtr, -1, rendererFlags).checkSDLError("SDL_CreateRenderer")
        return KRenderer(this, renderer)
    }

    fun messageBox(title: String, message: String, icon: KGraphics.MessageBoxIcon) {
        KGraphics.messageBox(title, message, icon, this)
    }


    enum class Mode {
        Windowed,
        FullScreen,
        FullScreenDesktop,
    }

}


