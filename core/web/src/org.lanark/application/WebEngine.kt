package org.lanark.application

import kotlinx.coroutines.*
import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.system.*
import org.w3c.dom.*
import kotlin.browser.*
import kotlin.coroutines.*

actual class Engine actual constructor(configure: EngineConfiguration.() -> Unit) {
    actual val logger: Logger

    actual val events = Signal<Event>("Event")
    actual val before = Signal<Unit>("BeforeIteration")
    actual val after = Signal<Unit>("AfterIteration")

    private val dpr = window.devicePixelRatio;

    private var scheduled = mutableListOf<suspend CoroutineScope.() -> Unit>()
    var running = false
        private set


    init {
        val configuration = EngineConfiguration("Web2D", 1, Version(1, 0, 0)).apply(configure)
        logger = configuration.logger ?: LoggerConsole()
        logger.info("Device pixel ratio: $dpr")
    }

    actual fun submit(task: suspend CoroutineScope.() -> Unit) {
        scheduled.add(task)
    }

    actual fun stop() {
        running = false
    }

    actual suspend fun run() {
        val scope = CoroutineScope(coroutineContext)
        //engine.logger.system("Running $this in $scope")
        running = true
        while (running) {
            //  engine.logger.system("Begin loop…")
            before.raise(Unit)
            //engine.logger.system("Done before.")
            if (!running) {
                coroutineContext.cancel()
                break
            }

            val launching = scheduled
            scheduled = mutableListOf()

            launching.forEach {
                //engine.logger.system("Launching $it")
                scope.launch {  it() }
            }

            if (!running) {
                coroutineContext.cancel()
                break
            }
            //engine.logger.system("Awaiting frame…")
            window.awaitAnimationFrame()
            //engine.logger.system("Got frame!")
            after.raise(Unit)
            //engine.logger.system("End loop.")
        }
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

actual suspend fun nextTick() : Double {
    return window.awaitAnimationFrame()
} 