package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.system.*
import org.w3c.dom.*
import org.w3c.performance.*
import kotlin.browser.*
import kotlin.coroutines.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger

    actual val events = Signal<Event>("Event")
    actual val before = Signal<Unit>("BeforeIteration")
    actual val after = Signal<Unit>("AfterIteration")

    private val dpr = window.devicePixelRatio;

    init {
        val configuration = EngineConfiguration("Web2D", 1, Version(1, 0, 0)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        logger.info("Device pixel ratio: $dpr")
    }

    private lateinit var engineContext: CoroutineContext
    actual fun run(main: suspend Engine.() -> Unit) = coroutineLoop {
        engineContext = kotlin.coroutines.coroutineContext + SupervisorJob()
        logger.info("Running application function")
        this@Engine.main()
        logger.info("Finished application function")
    }

    actual fun exitLoop() {
        engineContext.cancel()
    }

    actual fun createCoroutineScope(): CoroutineScope {
        return CoroutineScope(engineContext + SupervisorJob(engineContext[Job.Key]))
    }

    actual suspend fun loop() {
        logger.system("Running loop by $this")
        try {
            while (true) {
                before.raise(Unit)
                window.awaitAnimationFrame()
                if (!engineContext.isActive) // did anyone cancelled context?
                    return

                after.raise(Unit)

/*
                // swap all frames (may be check if they are dirty?)
                frames.forEach { e ->
                    e.value.present()
                }
*/

            }
        } finally {
            logger.system("Stopped loop by $this")
        }
    }

    actual suspend fun nextTick(): Float {
        val timeStarted = window.performance.now()
        val timeFinished = window.awaitAnimationFrame() 
        return ((timeFinished - timeStarted) / 1000.0).toFloat()
    }

    actual fun destroy() {}

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
        actual val LogCategory = LoggerCategory("Engine")
    }
}

