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

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit)  {
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
    private val frames = mutableMapOf<Long, Frame>()
    private val clock = Clock()

    private val metrics = Metrics()
    private val eventStats = metrics.reservoir("ApplicationTiming.event")
    private val updateStats = metrics.reservoir("ApplicationTiming.update")
    private val presentStats = metrics.reservoir("ApplicationTiming.present")
    private val statsClock = Clock()

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

    private lateinit var engineContext: CoroutineContext

    actual fun run(main: suspend Engine.() -> Unit) = coroutineLoop {
        engineContext = kotlin.coroutines.coroutineContext + SupervisorJob()
        logger.info("Running application function")
        this@Engine.main()
        logger.info("Finished application function")
    }

    actual suspend fun nextTick(): Float {
        val start = clock.elapsedMillis()
        yield()
        val end = clock.elapsedMillis()
        return (end - start).toInt().toFloat() / 1000f
    }

    actual fun createCoroutineScope(): CoroutineScope {
        return CoroutineScope(engineContext + SupervisorJob(engineContext[Job.Key]))
    }

    actual suspend fun loop() {
        logger.system("Running loop by $this")
        try {
            while (true) {
                val startLoopTime = clock.elapsedMillis()

                glfwPollEvents()

                val afterEventsTime = clock.elapsedMillis()

                before.raise(Unit)
                yield() // let other coroutines work
                if (!engineContext.isActive) // did anyone cancelled context?
                    return

                after.raise(Unit)

                val afterUpdateTime = clock.elapsedMillis()

                // swap all frames (may be check if they are dirty?)
                frames.forEach { _, frame ->
                    frame.present()
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
            frames[it.windowHandle] = it
            logger.system("Created $it")
            attachEvents(it)
        }
    }

    internal fun unregisterFrame(windowId: Long, frame: Frame) {
        val registered = frames[windowId]
        require(registered == frame) { "Window #$windowId must be unregistered with the same instance" }
        frames.remove(windowId)
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

    override fun toString() = "Engine(LWJGL3)"

    actual companion object {
        actual val LogCategory = LoggerCategory("Engine")
    }
}

