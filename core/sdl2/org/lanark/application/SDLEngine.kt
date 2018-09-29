package org.lanark.application

import kotlinx.cinterop.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import sdl2.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger
    actual val events: Events
    actual val executor: TaskExecutor
    
    private val version: Version
    private val mixVersion: Version
    private val platform: String
    private val displayWidth: Int
    private val displayHeight: Int
    private val refreshRate: Int
    private val cpus: Int
    private val memorySize: Int
    private val windows = mutableMapOf<UInt, Frame>()

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
        logger = configuration.logger
        events = configuration.events ?: Events(this)
        executor= configuration.executor ?: TaskExecutorIterative(this)

        logger.info("$platform with $cpus CPUs, $memorySize MB")
        logger.info("Initializing SDL v$version")
        SDL_Init(configuration.flags).sdlError("SDL_Init")
        logger.info("Enabled SDL subsystems: ${enabledSubsystems()}")

        val displayMode = memScoped {
            val displayMode = alloc<SDL_DisplayMode>()
            // TODO: Support multiply displays
            SDL_GetCurrentDisplayMode(0, displayMode.ptr.reinterpret()).sdlError("SDL_GetCurrentDisplayMode")
            logger.info("Display mode: ${displayMode.w}x${displayMode.h}, ${displayMode.refresh_rate} Hz")
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

    actual fun quit() {
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

    actual var activeCursor: Cursor?
        get() {
            val shown = SDL_ShowCursor(SDL_QUERY)
            if (shown == SDL_DISABLE)
                return null
            return SDL_GetCursor()?.let { Cursor(logger, it) } // TODO: store current cursor??
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

    actual fun sleep(millis: UInt) {
        SDL_Delay(millis)
    }

    fun messageBox(title: String, message: String, icon: MessageBoxIcon, parentWindow: Frame? = null) {
        SDL_ShowSimpleMessageBox(
            icon.flags,
            title,
            message,
            parentWindow?.windowPtr
        ).sdlError("SDL_ShowSimpleMessageBox")
    }

    actual fun setScreenSaver(enabled: Boolean) {
        if (enabled)
            SDL_EnableScreenSaver()
        else
            SDL_DisableScreenSaver()
    }

    actual fun createFrame(title: String, width: Int, height: Int, x: Int, y: Int, flags: FrameFlag): Frame {
        val sdlWindow = SDL_CreateWindow(title, x, y, width, height, flags.value).sdlError("SDL_CreateWindow")
        return Frame(this, sdlWindow).also {
            windows[it.id] = it
            logger.system("Created $it")
        }
    }

    actual fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor {
        val cursor = SDL_CreateColorCursor(canvas.surfacePtr, hotX, hotY).sdlError("SDL_CreateColorCursor")
        return Cursor(logger, cursor).also {
            logger.system("Created $it")
        }
    }

    actual fun createCursor(systemCursor: SystemCursor): Cursor {
        val cursor = SDL_CreateSystemCursor(systemCursor.handle).sdlError("SDL_CreateSystemCursor")
        return Cursor(logger, cursor).also {
            logger.system("Created $it")
        }
    }

    actual fun loadCanvas(path: String, fileSystem: FileSystem): Canvas {
        return fileSystem.open(path, FileOpenMode.Read).use { file ->
            val surfacePtr = IMG_Load_RW(file.handle, 0).sdlError("IMG_Load_RW")
            Canvas(this, surfacePtr).also {
                logger.system("Loaded $it from $path at $fileSystem")
            }
        }
    }

    actual fun createCanvas(size: Size, bitsPerPixel: Int): Canvas {
        val surface = SDL_CreateRGBSurface(0, size.width, size.height, bitsPerPixel, 0, 0, 0, 0).sdlError("SDL_CreateRGBSurface")
        return Canvas(this, surface).also {
            logger.system("Created $it")
        }
    }

    actual fun loadMusic(path: String, fileSystem: FileSystem): Music {
        return fileSystem.open(path, FileOpenMode.Read).use { file ->
            val audio = Mix_LoadMUS_RW(file.handle, 0).sdlError("Mix_LoadMUS_RW")
            Music(this, audio).also {
                logger.system("Loaded $it from $path at $fileSystem")
            }
        }
    }

    actual fun loadSound(path: String, fileSystem: FileSystem): Sound {
        return fileSystem.open(path, FileOpenMode.Read).use { file ->
            val audio = Mix_LoadWAV_RW(file.handle, 0).sdlError("Mix_LoadWAV_RW")
            Sound(this, audio).also {
                logger.system("Loaded $it from $path at $fileSystem")
            }
        }
    }

    actual fun loadVideo(path: String, fileSystem: FileSystem): Video {
        return fileSystem.open(path, FileOpenMode.Read).use {
            Video(this).also {
                logger.system("Loaded $it from $path at $fileSystem")
            }
        }
    }

    actual fun postQuitEvent() : Unit = memScoped{
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
}
