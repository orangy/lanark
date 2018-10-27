package org.lanark.ui

import org.lanark.application.*
import org.lanark.diagnostics.*
import org.lanark.events.*


class SceneApplication(val frame: Frame) {
    private val engine = frame.engine
    private val events = engine.events
    private val logger = engine.logger

    var scene: Scene? = null

    private var activeScene: Scene? = null

    private fun deactivate(activeScene: Scene?) {
        activeScene?.also { scene ->
            scene.deactivate(frame)
            this.activeScene = null
            logger.scene("Deactivated $scene")
        }
    }

    private fun activate(activeScene: Scene?) {
        activeScene?.also { scene ->
            logger.scene("Activating $scene")
            scene.activate(frame)
        }
    }

    private val activateSceneHandler: (Unit) -> Unit = {
        if (scene != activeScene) {
            deactivate(activeScene)
            activeScene = scene
            activate(activeScene)
        }
    }

    private val eventHandler: (Event) -> Unit = {
        activeScene?.event(frame, it)
    }

    private val renderHandler: (Unit) -> Unit = {
        frame.clip = null
        frame.clear()
        activeScene?.render(frame)
    }

    fun start(scene: Scene) {
        this.scene = scene

        engine.before.subscribe(activateSceneHandler)
        engine.after.subscribe(renderHandler)
        events.subscribe(eventHandler)
    }

    fun stop() {
        deactivate(activeScene)
        activeScene = null

        events.unsubscribe(eventHandler)
        engine.before.unsubscribe(activateSceneHandler)
        engine.after.unsubscribe(renderHandler)
    }

    companion object {
        val LogCategory = LoggerCategory("Composer")
    }
}

fun Logger.scene(message: String) = log(SceneApplication.LogCategory, message)
fun Logger.scene(message: () -> String) = log(SceneApplication.LogCategory, message)
