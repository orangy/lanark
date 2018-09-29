package org.lanark.application

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
import org.lwjgl.system.MemoryUtil.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger
    actual val events: Events
    actual val executor: TaskExecutor

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
    private val windows = mutableMapOf<UInt, Frame>()

    init {
        val configuration = EngineConfiguration(platform, cpus, version).apply(configure)
        logger = configuration.logger
        events = configuration.events ?: Events(this)
        executor = configuration.executor ?: TaskExecutorIterative(this)

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

    actual fun sleep(millis: UInt) {}
    actual fun setScreenSaver(enabled: Boolean) {}
    actual var activeCursor: Cursor?
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        flags: FrameFlag
    ): Frame {
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, if (FrameFlag.CreateResizable in flags) GLFW_TRUE else GLFW_FALSE) // the window will stay hidden after creation

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
        return Frame(this, window)
    }

    actual fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor {
        return Cursor(glfwCreateCursor(canvas.image, hotX, hotY))
    }

    actual fun createCursor(systemCursor: SystemCursor): Cursor {
        return Cursor(glfwCreateStandardCursor(systemCursor.cursorId))
    }

    actual fun createCanvas(size: Size, bitsPerPixel: Int): Canvas {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadCanvas(path: String, fileSystem: FileSystem): Canvas {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadMusic(path: String, fileSystem: FileSystem): Music {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadSound(path: String, fileSystem: FileSystem): Sound {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun loadVideo(path: String, fileSystem: FileSystem): Video {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}