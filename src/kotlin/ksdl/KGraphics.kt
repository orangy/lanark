package ksdl

import kotlinx.cinterop.*
import sdl2.*

object KGraphics {
    private val platform: String
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private var refreshRate: Int = 0

    init {
        SDL_Init(SDL_INIT_EVERYTHING).checkSDLError("SDL_Init")
        platform = SDL_GetPlatform()!!.toKString()
        memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()).checkSDLError("SDL_GetCurrentDisplayMode")
            displayWidth = displayMode.w
            displayHeight = displayMode.h
            refreshRate = displayMode.refresh_rate
        }
    }

    fun destroy() {
        SDL_Quit()
    }

    var activeCursor: KCursor?
        get() {
            val shown = SDL_ShowCursor(SDL_QUERY)
            if (shown == SDL_DISABLE)
                return null
            return SDL_GetCursor()?.let { KCursor(it) }
        }
        set(value) {
            if (value == null) {
                SDL_ShowCursor(SDL_DISABLE)
            } else {
                SDL_SetCursor(value.cursorPtr)
                SDL_ShowCursor(SDL_ENABLE)
            }
        }

    fun sleep(millis: Int) {
        SDL_Delay(millis)
    }

    fun messageBox(title: String, message: String, icon: MessageBoxIcon, parentWindow: KWindow? = null) {
        val flags = when (icon) {
            MessageBoxIcon.Information -> SDL_MESSAGEBOX_INFORMATION
            MessageBoxIcon.Warning -> SDL_MESSAGEBOX_WARNING
            MessageBoxIcon.Error -> SDL_MESSAGEBOX_ERROR
        }
        SDL_ShowSimpleMessageBox(flags, title, message, parentWindow?.windowPtr).checkSDLError("SDL_ShowSimpleMessageBox")
    }

    fun setScreenSaver(enabled: Boolean) {
        if (enabled)
            SDL_EnableScreenSaver()
        else
            SDL_DisableScreenSaver()
    }

    fun loadSurface(path: String): KSurface {
        val surface = IMG_Load(path).checkSDLError("IMG_Load")
        return KSurface(surface)
    }

    fun createWindow(caption: String, x: Int, y: Int, width: Int, height: Int, windowFlags: SDL_WindowFlags = SDL_WINDOW_SHOWN): KWindow {
        val window = SDL_CreateWindow(caption, x, y, width, height, windowFlags).checkSDLError("SDL_CreateWindow")
        return KWindow(window)
    }

    fun createCursor(systemCursor: SDL_SystemCursor): KCursor {
        val cursor = SDL_CreateSystemCursor(systemCursor).checkSDLError("SDL_CreateSystemCursor")
        return KCursor(cursor)
    }

    fun createCursor(surface: KSurface, hotX: Int, hotY: Int): KCursor {
        val cursor = SDL_CreateColorCursor(surface.surfacePtr, hotX, hotY).checkSDLError("SDL_CreateColorCursor")
        return KCursor(cursor)
    }

    enum class MessageBoxIcon {
        Information, Warning, Error
    }
}

