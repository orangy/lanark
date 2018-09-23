package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*

class SceneLayered(val name: String, val layers: List<Scene>) : Scene {
    override fun activate(executor: TaskExecutor) {
        layers.forEach {
            it.activate(executor)
        }
    }

    override fun deactivate(executor: TaskExecutor) {
        layers.forEach {
            it.deactivate(executor)
        }
    }

    override fun render(renderer: Renderer) {
        layers.forEach {
            it.render(renderer)
        }
    }

    override fun event(event: Event, executor: TaskExecutor): Boolean {
        return layers.asReversed().any { it.event(event, executor) }
    }

    override fun toString(): String = "SceneLayered($name)"
}