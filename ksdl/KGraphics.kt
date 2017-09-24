package ksdl

import kotlinx.cinterop.*
import sdl2.*

object KGraphics {
    private lateinit var platform: String
    private lateinit var log: KLog
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private var refreshRate: Int = 0

    fun init(configure: Configuration.() -> Unit) {
        val configuration = Configuration().apply(configure)
        init(configuration)
    }

    class Configuration {
        internal var flags = 0
        var log: KLog = KLogNone

        fun enableEverything() {
            flags = flags or SDL_INIT_EVERYTHING
        }

        fun enableTimer() {
            flags = flags or SDL_INIT_TIMER
        }

        fun enableAudio() {
            flags = flags or SDL_INIT_AUDIO
        }

        fun enableEvents() {
            flags = flags or SDL_INIT_EVENTS
        }

        fun enableController() {
            flags = flags or SDL_INIT_GAMECONTROLLER
        }

        fun enableHaptic() {
            flags = flags or SDL_INIT_HAPTIC
        }

        fun enableJoystick() {
            flags = flags or SDL_INIT_JOYSTICK
        }

        fun enableVideo() {
            flags = flags or SDL_INIT_VIDEO
        }
    }


    fun init(configuration: Configuration) {
        log = configuration.log
        log.log(KLog.Category.Trace, "Initializing SDL…")
        SDL_Init(configuration.flags).checkSDLError("SDL_Init")
        platform = SDL_GetPlatform()!!.toKString()
        log.log(KLog.Category.Trace, "Initialized '$platform', enabled: ${enabledSubsystems()}")
        memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            // TODO: Support multiply displays
            SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()).checkSDLError("SDL_GetCurrentDisplayMode")
            displayWidth = displayMode.w
            displayHeight = displayMode.h
            refreshRate = displayMode.refresh_rate
            log.log(KLog.Category.Trace, "Display mode: ${displayWidth}x$displayHeight,  $refreshRate Hz")
        }
    }

    private fun enabledSubsystems(): List<String> = mutableListOf<String>().apply {
        if (isVideoEnabled) add("Video")
        if (isAudioEnabled) add("Audio")
        if (isEventsEnabled) add("Events")
        if (isControllerEnabled) add("Controller")
        if (isHapticEnabled) add("Haptic")
        if (isJoystickEnabled) add("Joystick")
        if (isTimerEnabled) add("Timer")
    }

    fun destroy() {
        SDL_Quit()
    }

    val isVideoEnabled get() = SDL_WasInit(SDL_INIT_VIDEO) != 0
    val isAudioEnabled get() = SDL_WasInit(SDL_INIT_AUDIO) != 0
    val isTimerEnabled get() = SDL_WasInit(SDL_INIT_TIMER) != 0
    val isEventsEnabled get() = SDL_WasInit(SDL_INIT_EVENTS) != 0
    val isControllerEnabled get() = SDL_WasInit(SDL_INIT_GAMECONTROLLER) != 0
    val isJoystickEnabled get() = SDL_WasInit(SDL_INIT_JOYSTICK) != 0
    val isHapticEnabled get() = SDL_WasInit(SDL_INIT_HAPTIC) != 0

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

    fun createSurface(size: KSize, bitsPerPixel: Int): KSurface {
        val surface = SDL_CreateRGBSurface(0, size.width, size.height, bitsPerPixel, 0, 0, 0, 0).checkSDLError("SDL_CreateRGBSurface")
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

