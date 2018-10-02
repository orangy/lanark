package org.lanark.application

import org.khronos.webgl.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*
import org.w3c.dom.*
import kotlin.browser.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger
    actual val events: Events
    actual val executor: Executor

    private val context: WebGLRenderingContext
    private val document = window.document

    init {
        val canvas = document.getElementById("gl") as HTMLCanvasElement
        val gl = canvas.getContext("webgl")
            ?: canvas.getContext("experimental-webgl")
            ?: canvas.getContext("moz-webgl")
            ?: canvas.getContext("webkit-3d")
            ?: throw EngineException("No support for WebGL found.")

        context = gl as WebGLRenderingContext

        val vendor = context.getParameter(WebGLRenderingContext.VENDOR) as String
        val version = context.getParameter(WebGLRenderingContext.VERSION) as String
        val configuration = EngineConfiguration("WebGL [$vendor]", 1, Version.parse(version)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        events = configuration.events ?: Events(this)
        executor = configuration.executor ?: ExecutorCoroutines(this)

        logger.info("Initializing WebGL")
        val extensions = context.getSupportedExtensions()

        logger.system("Extensions: ${extensions?.joinToString(prefix = "[", postfix = "]") ?: "[]"}")

        val width = context.drawingBufferWidth
        val height = context.drawingBufferHeight
        logger.info("WebGL canvas size: ${width}x${height}")
    }

    actual fun quit() {}

    actual fun sleep(millis: UInt) {}
    actual fun setScreenSaver(enabled: Boolean) {}
    actual fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        flags: FrameFlag
    ): Frame {
        return Frame(this, context)
    }

    actual fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor? {
        return null
    }

    actual fun createCursor(systemCursor: SystemCursor): Cursor? {
        return null
    }

    actual fun createCanvas(size: Size, bitsPerPixel: Int): Canvas {
        return Canvas()
    }

    actual fun loadCanvas(path: String, fileSystem: FileSystem): Canvas {
        return Canvas()
    }

    actual fun loadMusic(path: String, fileSystem: FileSystem): Music {
        return Music()
    }

    actual fun loadSound(path: String, fileSystem: FileSystem): Sound {
        return Sound()
    }

    actual fun loadVideo(path: String, fileSystem: FileSystem): Video {
        return Video()
    }

    actual fun postQuitEvent() {}

}