package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL14.*
import org.lwjgl.stb.*
import org.lwjgl.system.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.glfw.GLFWImage

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger
    actual val events: Events
    actual val executor: Executor

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

    init {
        val configuration = EngineConfiguration(platform, cpus, version).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        events = configuration.events ?: Events(this)
        executor = configuration.executor ?: ExecutorCoroutines(this)

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

    actual fun quit() {
        glfwTerminate()
        logger.info("Quit LWJGL3")
    }

    actual fun sleep(millis: UInt) {
        TODO()
    }

    actual fun setScreenSaver(enabled: Boolean) {
        TODO()
    }
    
    actual fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        flags: FrameFlag
    ): Frame {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(
            GLFW_RESIZABLE,
            if (FrameFlag.CreateResizable in flags) GLFW_TRUE else GLFW_FALSE
        ) // the window will stay hidden after creation

        val monitor = if (FrameFlag.CreateFullscreen in flags)
            glfwGetPrimaryMonitor()
        else
            0L

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
            events.attachEvents(it)
        }
    }
    
    actual fun postQuitEvent() {
        events.all.raise(EventAppQuit(0u))
    }
    
    internal fun unregisterFrame(windowId: Long, frame: Frame) {
        val registered = windows[windowId]
        require(registered == frame) { "Window #$windowId must be unregistered with the same instance" }
        windows.remove(windowId)
    }
}

actual suspend fun nextTick() : Double {
    yield()
    return 0.0
} 