package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.system.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.system.MemoryUtil.*
import kotlin.coroutines.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val events = Signal<Event>("Events")
    actual val before = Signal<Unit>("BeforeIteration")
    actual val after = Signal<Unit>("AfterIteration")

    actual val logger: Logger

    private val version: Version = Version(
        org.lwjgl.Version.VERSION_MAJOR,
        org.lwjgl.Version.VERSION_MINOR,
        org.lwjgl.Version.VERSION_REVISION,
        org.lwjgl.Version.BUILD_TYPE.name
    )
    private val platform: String = "${System.getProperty("os.name")} v${System.getProperty("os.version")}"
    private val cpus: Int = Runtime.getRuntime().availableProcessors()
    private val memorySize: Long = Runtime.getRuntime().maxMemory() / 1024 / 1024

    private val displayWidth: Int
    private val displayHeight: Int
    private val refreshRate: Int
    private val windows = mutableMapOf<Long, Frame>()
    private val clock = Clock()

    private var scheduled = mutableListOf<suspend CoroutineScope.() -> Unit>()
    var running = false
        private set

    init {
        val configuration = EngineConfiguration(platform, cpus, version).apply(configure)
        logger = configuration.logger ?: LoggerConsole()

        logger.info("$platform with $cpus CPUs, $memorySize MB")
        logger.info("Initializing LWJGL3 v$version")

        GLFWErrorCallback.createPrint().set()

        if (!glfwInit())
            throw EngineException("Unable to initialize GLFW")

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_SAMPLES, 4)

        val monitor = glfwGetPrimaryMonitor()
        val vidmode = glfwGetVideoMode(monitor) ?: throw EngineException("Unable to get GLFW video mode")

        displayWidth = vidmode.width()
        displayHeight = vidmode.height()
        refreshRate = vidmode.refreshRate()
        logger.info("Display mode: ${displayWidth}x${displayHeight}, $refreshRate Hz")
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
        glfwTerminate()
        logger.info("Quit LWJGL3")
    }

    actual fun createFrame(title: String, width: Int, height: Int, x: Int, y: Int, flags: FrameFlag): Frame {
        val resizable = if (FrameFlag.CreateResizable in flags) GLFW_TRUE else GLFW_FALSE
        val monitor = if (FrameFlag.CreateFullscreen in flags) glfwGetPrimaryMonitor() else 0L

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resizable) 

        val window = glfwCreateWindow(width, height, title, monitor, NULL)
        if (window == NULL)
            throw EngineException("Failed to create the GLFW window")

        glfwMakeContextCurrent(window)
        GL.createCapabilities()
        glEnable(GL_TEXTURE_2D)
        glfwSwapInterval(1) // vsync

        if (FrameFlag.CreateVisible in flags)
            glfwShowWindow(window)
        
        return Frame(this, window).also {
            windows[it.windowHandle] = it
            logger.system("Created $it")
            attachEvents(it)
        }
    }

    internal fun unregisterFrame(windowId: Long, frame: Frame) {
        val registered = windows[windowId]
        require(registered == frame) { "Window #$windowId must be unregistered with the same instance" }
        windows.remove(windowId)
    }

    actual fun postQuitEvent() {
        events.raise(EventAppQuit(0u))
    }

    private fun attachEvents(frame: Frame) {
        glfwSetKeyCallback(frame.windowHandle) { windowId, key, scancode, action, mods ->
            val timestamp = clock.elapsedTicks()
            val event = when (action) {
                GLFW_PRESS -> EventKeyDown(timestamp, frame, key, scancode.toUInt(), false)
                GLFW_REPEAT -> EventKeyDown(timestamp, frame, key, scancode.toUInt(), false)
                GLFW_RELEASE -> EventKeyUp(timestamp, frame, key, scancode.toUInt())
                else -> throw EngineException("Unknown action $action")
            }
            events.raise(event)
        }

        glfwSetScrollCallback(frame.windowHandle) { window, dx, dy ->
            val timestamp = clock.elapsedTicks()
            events.raise(EventMouseScroll(timestamp, frame, dx.toInt(), dy.toInt()))
        }

        glfwSetMouseButtonCallback(frame.windowHandle) { window, button, action, mods ->
            val timestamp = clock.elapsedTicks()
            val x = DoubleArray(1)
            val y = DoubleArray(1)
            glfwGetCursorPos(frame.windowHandle, x, y)
            val mouseButton = when (button) {
                GLFW_MOUSE_BUTTON_LEFT -> MouseButton.Left
                GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.Middle
                GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.Right
                GLFW_MOUSE_BUTTON_4 -> MouseButton.X1
                GLFW_MOUSE_BUTTON_5 -> MouseButton.X2
                else -> return@glfwSetMouseButtonCallback // unknown button, skip the click
            }
            val clicks = 1u
            val posX = x[0].toInt()
            val posY = y[0].toInt()
            val event = when (action) {
                GLFW_PRESS -> EventMouseButtonDown(timestamp, frame, mouseButton, posX, posY, clicks)
                GLFW_RELEASE -> EventMouseButtonUp(timestamp, frame, mouseButton, posX, posY, clicks)
                else -> throw EngineException("Unrecognized mouse action")
            }
            events.raise(event)
        }

        glfwSetCursorPosCallback(frame.windowHandle) { window, xpos, ypos ->
            val timestamp = clock.elapsedTicks()
            events.raise(EventMouseMotion(timestamp, frame, xpos.toInt(), ypos.toInt(), 0, 0))
        }

        glfwSetWindowCloseCallback(frame.windowHandle) {
            val timestamp = clock.elapsedTicks()
            events.raise(EventWindowClose(timestamp, frame))
        }

        glfwSetWindowSizeCallback(frame.windowHandle) { windowId, width, height ->
            val timestamp = clock.elapsedTicks()
            events.raise(EventWindowSizeChanged(timestamp, frame, width, height))
        }
    }
    
    actual fun pollEvents() {
        glfwPollEvents()
    }

    actual companion object {
        actual val EventsLogCategory = LoggerCategory("Events")
    }
}

actual suspend fun nextTick(): Double {
    yield()
    return 0.0
} 