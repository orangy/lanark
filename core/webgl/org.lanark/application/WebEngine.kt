package org.lanark.application

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

    private val dpr = window.devicePixelRatio;

    init {

/*
        val context = canvas.getContext("webgl")
            ?: canvas.getContext("experimental-webgl")
            ?: canvas.getContext("moz-webgl")
            ?: canvas.getContext("webkit-3d")
            ?: throw EngineException("No support for WebGL found.")
*/


/*
        val vendor = context.getParameter(WebGLRenderingContext.VENDOR) as String
        val version = context.getParameter(WebGLRenderingContext.VERSION) as String
        val renderer = context.getParameter(WebGLRenderingContext.RENDERER) as String
*/
        val configuration = EngineConfiguration("Web2D", 1, Version(1, 0, 0)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        events = configuration.events ?: Events(this)
        executor = configuration.executor ?: WebExecutorCoroutines(this)

        logger.info("Device pixel ratio: $dpr")
    }

    actual fun quit() {
        
    }

    actual fun sleep(millis: UInt) {
        
    }
    
    actual fun setScreenSaver(enabled: Boolean) {
        
    }
    
    fun attachFrame(canvasId: String): Frame {
        val canvas = window.document.getElementById(canvasId) as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val boundingRect = canvas.getBoundingClientRect();
        logger.info("Canvas bounding rect: ${boundingRect.width}x${boundingRect.height}")
        logger.info("WebGL canvas size: ${canvas.width}x${canvas.height}")
        return Frame(this, context, Size(canvas.width, canvas.height))
    }
    
    actual fun createFrame(
        title: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int,
        flags: FrameFlag
    ): Frame {
        val canvas = window.document.getElementById("gl") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val boundingRect = canvas.getBoundingClientRect();
        logger.info("Canvas bounding rect: ${boundingRect.width}x${boundingRect.height}")
        
        canvas.width = (boundingRect.width * dpr).toInt()
        canvas.height = (boundingRect.height * dpr).toInt()
        logger.info("WebGL canvas size: ${canvas.width}x${canvas.height}")

        return Frame(this, context, Size(width, height))
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