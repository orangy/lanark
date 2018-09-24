package org.lanark.ui

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.system.*

class SceneApplication(val frame: Frame, val executor: TaskExecutor) {
    private val events = Events(frame.engine)
    private val metrics = Metrics()

    private val dumpStatsClock = Clock()
    private val statsClock = Clock()
    private val updateStats = metrics.reservoir("ApplicationTiming.update")
    private val renderStats = metrics.reservoir("ApplicationTiming.render")
    private val presentStats = metrics.reservoir("ApplicationTiming.present")

    var scene: Scene? = null

    private var activeScene: Scene? = null

    private fun deactivate(activeScene: Scene?) {
        activeScene?.also { scene ->
            scene.deactivate(executor)
            this.activeScene = null
            frame.engine.logger.composer("Deactivated $scene")
        }
    }

    private fun activate(activeScene: Scene?) {
        activeScene?.also { scene ->
            frame.engine.logger.composer("Activating $scene")
            scene.activate(executor)
        }
    }

    private val beforeHandler: (Unit) -> Unit = {
        statsClock.reset()
        if (scene != activeScene) {
            deactivate(activeScene)
            activeScene = scene
            activate(activeScene)
        }
        events.poll()
    }

    private val afterHandler: (Unit) -> Unit = {
        val time1 = statsClock.elapsedMicros()
        val renderer = frame.renderer
        
        renderer.clip = null
        renderer.clear(Color.BLACK)
        activeScene?.render(renderer)
        val time2 = statsClock.elapsedMicros()
        renderer.present()
        val time3 = statsClock.elapsedMicros()

        updateStats.update(time1.toLong())
        renderStats.update((time2 - time1).toLong())
        presentStats.update((time3 - time2).toLong())

        if (dumpStatsClock.elapsedSeconds() > 10u) {
            dumpStatsClock.reset()
            val meanUpdate = updateStats.snapshot().mean()
            val meanRender = renderStats.snapshot().mean()
            val meanPresent = presentStats.snapshot().mean()
            frame.engine.logger.system("Mean times: U[${round(meanUpdate, 2)}] R[${round(meanRender, 2)}] P[${round(meanPresent, 2)}]")
        }
    }

    fun run() {
        executor.before.subscribe(beforeHandler)
        executor.after.subscribe(afterHandler)

        events.keyboard.subscribe { activeScene?.event(it, executor) }
        events.mouse.subscribe { activeScene?.event(it, executor) }
        events.window.subscribe {
            // TODO: Decide on automatic resizing?
            if (it is EventWindowResized) {
                frame.renderer.size = Size(it.width, it.height)
            }
        }

        events.application.subscribe {
            when (it) {
                is EventAppQuit -> executor.stop()
            }
        }

        executor.run()
        deactivate(activeScene)
        activeScene = null

        executor.before.unsubscribe(beforeHandler)
        executor.after.unsubscribe(afterHandler)
    }

    companion object {
        val LogCategory = LoggerCategory("Composer")
    }
}

fun Logger.composer(message: String) = log(SceneApplication.LogCategory, message)
