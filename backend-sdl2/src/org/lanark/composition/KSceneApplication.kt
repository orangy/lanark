package org.lanark.composition

import org.lanark.diagnostics.*
import org.lanark.events.*
import org.lanark.geometry.*
import org.lanark.metrics.*
import org.lanark.rendering.*
import org.lanark.system.*

class KSceneApplication(val executor: KTaskExecutor, val renderer: Renderer) {
    private val events = KEvents()

    private val dumpStatsClock = Clock()
    private val statsClock = Clock()
    private val updateStats = platform.metrics.reservoir("ApplicationTiming.update")
    private val renderStats = platform.metrics.reservoir("ApplicationTiming.render")
    private val presentStats = platform.metrics.reservoir("ApplicationTiming.present")

    var scene: KScene? = null

    private var activeScene: KScene? = null

    private fun deactivate(activeScene: KScene?) {
        activeScene?.also { scene ->
            scene.deactivate(executor)
            this.activeScene = null
            logger.composer("Deactivated $scene")
        }
    }

    private fun activate(activeScene: KScene?) {
        activeScene?.also { scene ->
            logger.composer("Activating $scene")
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
        events.pollEvents()
    }

    private val afterHandler: (Unit) -> Unit = {
        val time1 = statsClock.elapsedMicros()
        renderer.clip = null
        renderer.clear(KColor.BLACK)
        scene?.render(renderer)
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
            logger.system("Mean times: U[${KMath.round(meanUpdate, 2)}] R[${KMath.round(meanRender, 2)}] P[${KMath.round(meanPresent, 2)}]")
        }
    }

    fun run() {
        executor.before.subscribe(beforeHandler)
        executor.after.subscribe(afterHandler)

        events.keyboard.subscribe { activeScene?.event(it, executor) }
        events.mouse.subscribe { activeScene?.event(it, executor) }
        events.window.subscribe {
            // TODO: Decide on automatic resizing?
            if (it is KEventWindowResized) {
                renderer.size = KSize(it.width, it.height)
            }
        }

        events.application.subscribe {
            when (it) {
                is KEventAppQuit -> executor.stop()
            }
        }

        executor.run()
        deactivate(activeScene)
        activeScene = null

        executor.before.unsubscribe(beforeHandler)
        executor.after.unsubscribe(afterHandler)
    }

    companion object {
        val LogCategory = KLogCategory("Composer")
    }
}

fun KLogger.composer(message: String) = log(KSceneApplication.LogCategory, message)
