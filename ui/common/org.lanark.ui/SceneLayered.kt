package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*

class SceneLayered(val name: String, val layers: List<Scene>) : Scene {
    override fun activate(frame: Frame) {
        layers.forEach {
            it.activate(frame)
        }
    }

    override fun deactivate(frame: Frame) {
        layers.forEach {
            it.deactivate(frame)
        }
    }

    override fun render(frame: Frame) {
        layers.forEach {
            it.render(frame)
        }
    }

    override fun event(frame: Frame, event: Event): Boolean {
        return layers.asReversed().any { it.event(frame, event) }
    }

    override fun toString(): String = "SceneLayered($name)"
}