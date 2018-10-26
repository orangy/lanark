package org.lanark.application

import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.system.*
import org.w3c.dom.*
import kotlin.browser.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger

    actual val events: Signal<Event> = Signal("Event")
    actual val executor: Executor = WebExecutorCoroutines(this)

    private val dpr = window.devicePixelRatio;

    init {
        val configuration = EngineConfiguration("Web2D", 1, Version(1, 0, 0)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        logger.info("Device pixel ratio: $dpr")
    }

    actual fun pollEvents() {}
    actual fun postQuitEvent() {}
    actual fun quit() {}

    fun attachFrame(canvasId: String): Frame {
        val canvas = window.document.getElementById(canvasId) as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val boundingRect = canvas.getBoundingClientRect();
        logger.info("Canvas bounding rect: ${boundingRect.width}x${boundingRect.height}")
        logger.info("WebGL canvas size: ${canvas.width}x${canvas.height}")
        return Frame(this, context, Size(canvas.width, canvas.height))
    }

    actual fun createFrame(title: String, width: Int, height: Int, x: Int, y: Int, flags: FrameFlag): Frame {
        val canvas = window.document.getElementById("gl") as HTMLCanvasElement
        val context = canvas.getContext("2d") as CanvasRenderingContext2D
        val boundingRect = canvas.getBoundingClientRect();
        logger.info("Canvas bounding rect: ${boundingRect.width}x${boundingRect.height}")

        canvas.width = (boundingRect.width * dpr).toInt()
        canvas.height = (boundingRect.height * dpr).toInt()
        logger.info("WebGL canvas size: ${canvas.width}x${canvas.height}")

        return Frame(this, context, Size(width, height))
    }

    actual companion object {
        actual val EventsLogCategory = LoggerCategory("Events")
    }

}