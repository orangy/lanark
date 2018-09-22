package ksdl.system

import kotlinx.cinterop.*
import ksdl.diagnostics.*
import ksdl.rendering.*
import sdl2.*
import kotlin.collections.*

private lateinit var platformInstance: KPlatform

val platform get() = platformInstance

class KPlatform(configure: Configuration.() -> Unit) {
    val logger: KLogger

    private val version: KVersion
    private val mixVersion: KVersion
    private val platform: String
    private val displayWidth: Int
    private val displayHeight: Int
    private val refreshRate: Int
    private val cpus: Int
    private val memorySize: Int
    private val windows = mutableMapOf<UInt, KWindow>()

    init {
        version = memScoped {
            val version = alloc<SDL_version>()
            SDL_GetVersion(version.ptr)
            KVersion(version.major.toInt(), version.minor.toInt(), version.patch.toInt(), SDL_GetRevision()?.toKString()
                    ?: "null")
        }
        mixVersion = memScoped {
            val mixApiVersion = Mix_Linked_Version().checkSDLError("Mix_Linked_Version")
            val version = mixApiVersion.pointed
            KVersion(version.major.toInt(), version.minor.toInt(), version.patch.toInt())
        }

        platform = SDL_GetPlatform()!!.toKString()
        cpus = SDL_GetCPUCount()
        memorySize = SDL_GetSystemRAM()

        val configuration = Configuration(platform, cpus, version).apply(configure)
        logger = configuration.logger

        logger.info("$platform with $cpus CPUs, $memorySize MB RAM...")
        logger.info("Initializing SDL v$version")
        SDL_Init(configuration.flags).checkSDLError("SDL_Init")
        logger.info("Enabled SDL subsystems: ${enabledSubsystems()}")

        val displayMode = memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            // TODO: Support multiply displays
            SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()).checkSDLError("SDL_GetCurrentDisplayMode")
            logger.info("Display mode: ${displayMode.w}x${displayMode.h}, ${displayMode.refresh_rate} Hz")
            displayMode
        }

        displayWidth = displayMode.w
        displayHeight = displayMode.h
        refreshRate = displayMode.refresh_rate

        logger.info("Initializing MIX v$mixVersion")
        Mix_Init(MIX_INIT_OGG.toInt()).checkSDLError("Mix_Init")
        Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT, 1, 4096).checkSDLError("Mix_OpenAudio")
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

    val isVideoEnabled get() = SDL_WasInit(SDL_INIT_VIDEO) != 0u
    val isAudioEnabled get() = SDL_WasInit(SDL_INIT_AUDIO) != 0u
    val isTimerEnabled get() = SDL_WasInit(SDL_INIT_TIMER) != 0u
    val isEventsEnabled get() = SDL_WasInit(SDL_INIT_EVENTS) != 0u
    val isControllerEnabled get() = SDL_WasInit(SDL_INIT_GAMECONTROLLER) != 0u
    val isJoystickEnabled get() = SDL_WasInit(SDL_INIT_JOYSTICK) != 0u
    val isHapticEnabled get() = SDL_WasInit(SDL_INIT_HAPTIC) != 0u

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

    fun sleep(millis: UInt) {
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

    internal fun registerWindow(windowID: UInt, window: KWindow) {
        windows[windowID] = window
    }

    internal fun unregisterWindow(windowID: UInt, window: KWindow) {
        val registered = windows[windowID]
        require(registered == window) { "Window #$windowID must be unregistered with the same instance" }
        windows.remove(windowID)
    }

    fun findWindow(windowID: UInt) = windows[windowID]

    enum class MessageBoxIcon {
        Information, Warning, Error
    }

    class Configuration(val platform: String, val cpus: Int, val version: KVersion) {
        internal var flags: UInt = 0u
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

    companion object {
        fun init(configure: Configuration.() -> Unit) {
            platformInstance = KPlatform(configure)
        }
    }
}

val logger: KLogger
    inline get() = platform.logger