package org.lanark.application

import kotlinx.cinterop.*
import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.system.*
import sdl2.*
import kotlin.coroutines.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger

    actual val events = Signal<Event>("Event")
    actual val before = Signal<Unit>("BeforeIteration")
    actual val after = Signal<Unit>("AfterIteration")

    val displayWidth: Int
    val displayHeight: Int

    private val version: Version
    private val mixVersion: Version
    private val platform: String
    private val refreshRate: Int
    private val cpus: Int
    private val memorySize: Int
    private val windows = mutableMapOf<UInt, Frame>()

    private var scheduled = mutableListOf<suspend CoroutineScope.() -> Unit>()
    var running = false
        private set
    
    init {
        version = memScoped {
            val version = alloc<SDL_version>()
            SDL_GetVersion(version.ptr)
            Version(
                version.major.toInt(), version.minor.toInt(), version.patch.toInt(), SDL_GetRevision()?.toKString()
                    ?: "null"
            )
        }

        mixVersion = memScoped {
            val mixApiVersion = Mix_Linked_Version().sdlError("Mix_Linked_Version")
            val version = mixApiVersion.pointed
            Version(version.major.toInt(), version.minor.toInt(), version.patch.toInt())
        }

        platform = SDL_GetPlatform()!!.toKString()
        cpus = SDL_GetCPUCount()
        memorySize = SDL_GetSystemRAM()

        val configuration = EngineConfiguration(platform, cpus, version).apply(configure)
        logger = configuration.logger ?: NSLogger()

        logger.info("$platform with $cpus CPUs, $memorySize MB")
        logger.info("Initializing SDL v$version")
        SDL_Init(configuration.flags).sdlError("SDL_Init")
        logger.info("Enabled SDL subsystems: ${enabledSubsystems()}")

        val displayMode = memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            val bounds = alloc<SDL_Rect>()
            val usable = alloc<SDL_Rect>()
            val count = SDL_GetNumVideoDisplays()
            repeat(count) { display ->
                val name = SDL_GetDisplayName(display)
                SDL_GetDisplayBounds(display, bounds.ptr)
                SDL_GetDisplayUsableBounds(display, usable.ptr)
                val usableRect = Rect(usable.x, usable.y, usable.w, usable.h)
                val boundsRect = Rect(bounds.x, bounds.y, bounds.w, bounds.h)
                logger.info("Display #$display: $name, $boundsRect, usable $usableRect")
                SDL_GetDesktopDisplayMode(display, displayMode.ptr)
                logger.info("  Desktop display mode: ${displayMode.w}x${displayMode.h}, ${displayMode.refresh_rate} Hz")

                val modes = SDL_GetNumDisplayModes(display)
                repeat(modes) { mode ->
                    SDL_GetDisplayMode(display, mode, displayMode.ptr).sdlError("SDL_GetCurrentDisplayMode")
                    logger.info("  Display mode #$mode: ${displayMode.w}x${displayMode.h}, ${displayMode.refresh_rate} Hz")
                }
            }

            SDL_GetCurrentDisplayMode(0, displayMode.ptr).sdlError("SDL_GetCurrentDisplayMode")
            logger.info("Current display mode: ${displayMode.w}x${displayMode.h}, ${displayMode.refresh_rate} Hz")
            displayMode
        }

        displayWidth = displayMode.w
        displayHeight = displayMode.h
        refreshRate = displayMode.refresh_rate

        logger.info("Initializing MIX v$mixVersion")
        Mix_Init(MIX_INIT_MP3.toInt()).sdlError("Mix_Init")
        Mix_OpenAudio(MIX_DEFAULT_FREQUENCY, MIX_DEFAULT_FORMAT, 1, 4096).sdlError("Mix_OpenAudio")
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

    actual fun submit(task: suspend CoroutineScope.() -> Unit) {
        scheduled.add(task)
    }

    actual suspend fun run() {
        val scope = CoroutineScope(kotlin.coroutines.coroutineContext)
        logger.system("Running $this in $scope")
        running = true
        while (running) {
            before.raise(Unit)
            if (!running) {
                coroutineContext.cancel()
                break
            }

            val launching = scheduled
            scheduled = mutableListOf()

            launching.forEach {
                scope.launch {  it() }
            }

            yield()

            if (!running) {
                coroutineContext.cancel()
                break
            }
            after.raise(Unit) // vsync
        }
        logger.system("Stopped $this")
    }

    actual fun stop() {
        running = false
    }

    actual fun quit() {
        Mix_Quit()
        logger.info("Quit MIX")
        SDL_Quit()
        logger.info("Quit SDL")
    }

    val isMouseEnabled get() = SDL_GetDefaultCursor() != null
    val isVideoEnabled get() = SDL_WasInit(SDL_INIT_VIDEO) != 0u
    val isAudioEnabled get() = SDL_WasInit(SDL_INIT_AUDIO) != 0u
    val isTimerEnabled get() = SDL_WasInit(SDL_INIT_TIMER) != 0u
    val isEventsEnabled get() = SDL_WasInit(SDL_INIT_EVENTS) != 0u
    val isControllerEnabled get() = SDL_WasInit(SDL_INIT_GAMECONTROLLER) != 0u
    val isJoystickEnabled get() = SDL_WasInit(SDL_INIT_JOYSTICK) != 0u
    val isHapticEnabled get() = SDL_WasInit(SDL_INIT_HAPTIC) != 0u
    
    fun messageBox(title: String, message: String, icon: MessageBoxIcon, parentWindow: Frame? = null) {
        SDL_ShowSimpleMessageBox(
            icon.flags,
            title,
            message,
            parentWindow?.windowPtr
        ).sdlError("SDL_ShowSimpleMessageBox")
    }
    
    actual fun createFrame(title: String, width: Int, height: Int, x: Int, y: Int, flags: FrameFlag): Frame {
        val sdlWindow = SDL_CreateWindow(title, x, y, width, height, flags.value).sdlError("SDL_CreateWindow")
        return Frame(this, sdlWindow).also {
            windows[it.id] = it
            logger.system("Created $it")
        }
    }

    actual fun pollEvents() = memScoped {
        val sdlEvent = alloc<SDL_Event>()
        while (SDL_PollEvent(sdlEvent.ptr) == 1) {
            val event = when (sdlEvent.type) {
                SDL_QUIT,
                SDL_APP_TERMINATING, SDL_APP_LOWMEMORY, SDL_APP_DIDENTERBACKGROUND,
                SDL_APP_DIDENTERFOREGROUND, SDL_APP_WILLENTERBACKGROUND, SDL_APP_WILLENTERFOREGROUND -> 
                    createAppEvent(sdlEvent, this@Engine)
                SDL_WINDOWEVENT -> 
                    createWindowEvent(sdlEvent, this@Engine)
                SDL_KEYUP, SDL_KEYDOWN -> 
                    createKeyEvent(sdlEvent, this@Engine)
                SDL_MOUSEBUTTONDOWN, SDL_MOUSEBUTTONUP, SDL_MOUSEMOTION, SDL_MOUSEWHEEL -> 
                    createMouseEvent(sdlEvent, this@Engine)
                SDL_FINGERMOTION, SDL_FINGERDOWN, SDL_FINGERUP -> {
                    // ignore event and don't log it
                    null
                }
                else -> {
                    val eventName = sdlEventNames[sdlEvent.type]
                    if (eventName == null)
                        logger.event { "Unknown event: ${sdlEvent.type}" }
                    else
                        logger.event { eventName.toString() }
                    null
                }
            }
            
            if (event != null) {
                logger.event { event.toString() }
                events.raise(event)
            }
        }
    }


    actual fun postQuitEvent(): Unit = memScoped {
        val event = alloc<SDL_Event>()
        event.type = SDL_QUIT
        SDL_PushEvent(event.ptr)
    }

    internal fun unregisterFrame(windowId: UInt, frame: Frame) {
        val registered = windows[windowId]
        require(registered == frame) { "Window #$windowId must be unregistered with the same instance" }
        windows.remove(windowId)
    }

    fun tryGetFrame(windowId: UInt) = windows[windowId]

    fun getFrame(windowId: UInt) = windows[windowId] ?: throw EngineException("Cannot find Frame for ID $windowId")
    
    actual companion object {
        actual val EventsLogCategory = LoggerCategory("Events")
    }
}

actual suspend fun nextTick(): Double {
    yield()
    return 0.0
} 
