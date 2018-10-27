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
    private val frames = mutableMapOf<UInt, Frame>()
    private val clock = Clock()

    private val metrics = Metrics()
    private val eventStats = metrics.reservoir("ApplicationTiming.event")
    private val updateStats = metrics.reservoir("ApplicationTiming.update")
    private val presentStats = metrics.reservoir("ApplicationTiming.present")
    private val statsClock = Clock()

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

    private lateinit var engineContext: CoroutineContext
    actual fun run(main: suspend Engine.() -> Unit) = coroutineLoop {
        try {
            logger.engine("Capturing execution context…")
            engineContext = kotlin.coroutines.coroutineContext + SupervisorJob()
            logger.info("Running application function")
            this@Engine.main()
            logger.info("Finished application function")
        } catch (e: Throwable) {
            logger.error(e.message ?: "<no error>")
        }
    }

    actual fun createCoroutineScope(): CoroutineScope {
        return CoroutineScope(engineContext + SupervisorJob(engineContext[Job.Key]))
    }

    actual suspend fun nextTick(): Float {
        val start = clock.elapsedMillis()
        yield()
        val end = clock.elapsedMillis()
        return (end - start).toInt().toFloat() / 1000f
    }

    actual suspend fun loop() {
        logger.system("Running loop by $this")
        try {
            while (true) {
                val startLoopTime = clock.elapsedMillis()

                pollEvents()
                val afterEventsTime = clock.elapsedMillis()

                before.raise(Unit)
                yield() // let other coroutines work
                if (!engineContext.isActive) // did anyone cancelled context?
                    return

                after.raise(Unit)

                val afterUpdateTime = clock.elapsedMillis()

                // swap all frames (may be check if they are dirty?)
                frames.forEach { e ->
                    e.value.present()
                }

                val afterPresentTime = clock.elapsedMillis()
                eventStats.update((afterEventsTime - startLoopTime).toLong())
                updateStats.update((afterUpdateTime - afterEventsTime).toLong())
                presentStats.update((afterPresentTime - afterUpdateTime).toLong())
                dumpStatistics()
            }
        } finally {
            logger.system("Stopped loop by $this")
        }
    }

    private fun dumpStatistics() {
        if (statsClock.elapsedSeconds() <= 10u)
            return

        val meanEvents = eventStats.snapshot().mean()
        val meanUpdate = updateStats.snapshot().mean()
        val meanPresent = presentStats.snapshot().mean()
        logger.system(
            "Mean times: E[${round(meanEvents, 2)} ms] U[${round(meanUpdate, 2)} ms] P[${round(meanPresent, 2)} ms]"
        )
        statsClock.reset()
    }

    actual fun exitLoop() {
        engineContext.cancel()
    }

    actual fun destroy() {
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
            frames[it.id] = it
            logger.system("Created $it")
        }
    }

    private fun pollEvents() = memScoped {
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
                        logger.engine { "Unknown event: ${sdlEvent.type}" }
                    else
                        logger.engine { eventName.toString() }
                    null
                }
            }

            if (event != null) {
                logger.engine { event.toString() }
                events.raise(event)
            }
        }
    }


    internal fun unregisterFrame(windowId: UInt, frame: Frame) {
        val registered = frames[windowId]
        require(registered == frame) { "Window #$windowId must be unregistered with the same instance" }
        frames.remove(windowId)
    }

    fun tryGetFrame(windowId: UInt) = frames[windowId]

    fun getFrame(windowId: UInt) = frames[windowId] ?: throw EngineException("Cannot find Frame for ID $windowId")

    actual companion object {
        actual val LogCategory = LoggerCategory("Engine")
    }
}
