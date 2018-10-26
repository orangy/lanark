package org.lanark.ui

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.drawing.*
import org.lanark.events.*
import org.lanark.system.*


class SceneApplication(val frame: Frame)  {
    private val metrics = Metrics()
    private val engine = frame.engine
    private val events = engine.events
    private val logger = engine.logger

    private val dumpStatsClock = Clock()
    private val statsClock = Clock()
    private val updateStats = metrics.reservoir("ApplicationTiming.update")
    private val renderStats = metrics.reservoir("ApplicationTiming.render")
    private val presentStats = metrics.reservoir("ApplicationTiming.present")

    var scene: Scene? = null

    private var activeScene: Scene? = null

    private fun deactivate(activeScene: Scene?) {
        activeScene?.also { scene ->
            scene.deactivate(frame)
            this.activeScene = null
            logger.composer("Deactivated $scene")
        }
    }

    private fun activate(activeScene: Scene?) {
        activeScene?.also { scene ->
            logger.composer("Activating $scene")
            scene.activate(frame)
        }
    }

    private val beforeHandler: (Unit) -> Unit = {
        statsClock.reset()
        if (scene != activeScene) {
            deactivate(activeScene)
            activeScene = scene
            activate(activeScene)
        }
        engine.pollEvents()
    }

    private val afterHandler: (Unit) -> Unit = {
        val time1 = statsClock.elapsedMicros()

        frame.clip = null
        frame.clear(Color.BLACK)
        activeScene?.render(frame)
        val time2 = statsClock.elapsedMicros()
        frame.present()
        val time3 = statsClock.elapsedMicros()

        updateStats.update(time1.toLong())
        renderStats.update((time2 - time1).toLong())
        presentStats.update((time3 - time2).toLong())

        if (dumpStatsClock.elapsedSeconds() > 10u) {
            dumpStatsClock.reset()
            val meanUpdate = updateStats.snapshot().mean()
            val meanRender = renderStats.snapshot().mean()
            val meanPresent = presentStats.snapshot().mean()
            logger.system("Mean times: U[${round(meanUpdate, 2)}] R[${round(meanRender, 2)}] P[${round(meanPresent, 2)}]")
        }
    }

    suspend fun run() {
        engine.before.subscribe(beforeHandler)
        engine.after.subscribe(afterHandler)

        events.subscribe { activeScene?.event(frame, it) }
        events.filter<EventAppQuit>().subscribe { engine.stop() }

        engine.run()
        deactivate(activeScene)
        activeScene = null

        engine.before.unsubscribe(beforeHandler)
        engine.after.unsubscribe(afterHandler)
    }

    companion object {
        val LogCategory = LoggerCategory("Composer")
    }
}

fun Logger.composer(message: String) = log(SceneApplication.LogCategory, message)
