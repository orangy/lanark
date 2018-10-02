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

    init {
        val document = window.document
        val canvas = document.getElementById("gl") as HTMLCanvasElement
        val gl = canvas.getContext("webgl")
            ?: canvas.getContext("experimental-webgl")
            ?: canvas.getContext("moz-webgl")
            ?: canvas.getContext("webkit-3d")
            ?: throw EngineException("No support for WebGL found.")

        gl as WebGLRenderingContext
        
        val vendor = gl.getParameter(WebGLRenderingContext.VENDOR) as String
        val version = gl.getParameter(WebGLRenderingContext.VERSION) as String
        val configuration = EngineConfiguration("WebGL [$vendor]", 1, Version.parse(version)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        events = configuration.events ?: Events(this)
        executor = configuration.executor ?: ExecutorCoroutines(this)

        logger.info("Initializing WebGL")
        val extensions = gl.getSupportedExtensions()
        console.log(gl)
        console.log(extensions)

        val width = gl.drawingBufferWidth
        val height = gl.drawingBufferHeight
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createCursor(canvas: Canvas, hotX: Int, hotY: Int): Cursor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun createCursor(systemCursor: SystemCursor): Cursor? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    actual fun postQuitEvent() {}

}