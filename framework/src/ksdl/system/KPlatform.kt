package ksdl.system

import kotlinx.cinterop.*
import kotlinx.cinterop.CPointer
import ksdl.diagnostics.*
import ksdl.rendering.KCursor
import sdl2.*

object KPlatform {
    private val version: KVersion
    private val mixVersion: KVersion
    private lateinit var platform: String
    lateinit var logger: KLogger
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    private var refreshRate: Int = 0
    private var cpus: Int = 0
    private var memorySize: Int = 0
    private val windows = mutableMapOf<Int, KWindow>()

    init {
        version = memScoped {
            val version = alloc<SDL_version>()
            SDL_GetVersion(version.ptr)
            KVersion(version.major.toInt(), version.minor.toInt(), version.patch.toInt(), SDL_GetRevision()?.toKString() ?: "null")
        }
        mixVersion = memScoped {
            val mixApiVersion = Mix_Linked_Version().checkSDLError("Mix_Linked_Version")
            val version = mixApiVersion.pointed
            KVersion(version.major.toInt(), version.minor.toInt(), version.patch.toInt())
        }
    }

    fun init(configure: Configuration.() -> Unit) {
        platform = SDL_GetPlatform()!!.toKString()
        cpus = SDL_GetCPUCount()
        memorySize = SDL_GetSystemRAM()

        val configuration = Configuration(platform, cpus, version).apply(configure)
        logger = configuration.logger

        logger.info("$platform with $cpus CPUs, $memorySize MB RAM...")
        logger.info("Initializing SDL v$version")
        SDL_Init(configuration.flags).checkSDLError("SDL_Init")
        logger.info("Enabled SDL subsystems: ${enabledSubsystems()}")

        memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            // TODO: Support multiply displays
            SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()).checkSDLError("SDL_GetCurrentDisplayMode")
            displayWidth = displayMode.w
            displayHeight = displayMode.h
            refreshRate = displayMode.refresh_rate
            logger.info("Display mode: ${displayWidth}x${displayHeight}, $refreshRate Hz")
        }

        logger.info("Initializing MIX v$mixVersion")
        Mix_Init(MIX_INIT_OGG).checkSDLError("Mix_Init")
        Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT.toShort(), 1, 4096).checkSDLError("Mix_OpenAudio")
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

    fun quit() {
        Mix_Quit()
        logger.info("Quit MIX")
        SDL_Quit()
        logger.info("Quit SDL")
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
                val shown = SDL_ShowCursor(SDL_QUERY)
                if (shown != SDL_DISABLE)
                    SDL_ShowCursor(SDL_DISABLE)
            } else {
                val shown = SDL_ShowCursor(SDL_QUERY)
                if (shown == SDL_DISABLE)
                    SDL_ShowCursor(SDL_ENABLE)
                val oldCursor = SDL_GetCursor()
                if (oldCursor != value.cursorPtr)
                    SDL_SetCursor(value.cursorPtr)
            }
        }

    fun sleep(millis: Int) {
        SDL_Delay(millis)
    }

    fun messageBox(title: String, message: String, icon: MessageBoxIcon, parentWindow: CPointer<SDL_Window>? = null) {
        val flags = when (icon) {
            MessageBoxIcon.Information -> SDL_MESSAGEBOX_INFORMATION
            MessageBoxIcon.Warning -> SDL_MESSAGEBOX_WARNING
            MessageBoxIcon.Error -> SDL_MESSAGEBOX_ERROR
        }
        SDL_ShowSimpleMessageBox(flags, title, message, parentWindow).checkSDLError("SDL_ShowSimpleMessageBox")
    }

    fun setScreenSaver(enabled: Boolean) {
        if (enabled)
            SDL_EnableScreenSaver()
        else
            SDL_DisableScreenSaver()
    }

    internal fun registerWindow(windowID: Int, window: KWindow) {
        windows[windowID] = window
    }

    internal fun unregisterWindow(windowID: Int, window: KWindow) {
        val registered = windows[windowID]
        require(registered == window) { "Window #$windowID must be unregistered with the same instance" }
        windows.remove(windowID)
    }

    fun findWindow(windowID: Int) = windows[windowID]

    enum class MessageBoxIcon {
        Information, Warning, Error
    }

    class Configuration(val platform: String, val cpus: Int, val version: KVersion) {
        internal var flags = 0
        var logger: KLogger = KLoggerNone

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
}

val logger: KLogger
    inline get() = KPlatform.logger