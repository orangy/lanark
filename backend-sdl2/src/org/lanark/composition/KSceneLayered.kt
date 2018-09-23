package org.lanark.composition

import org.lanark.events.*
import org.lanark.rendering.*
import org.lanark.system.*

class KSceneLayered(val name: String, val layers: List<KScene>) : KScene {
    override fun activate(executor: KTaskExecutor) {
        layers.forEach {
            it.activate(executor)
        }
    }

    override fun deactivate(executor: KTaskExecutor) {
        layers.forEach {
            it.deactivate(executor)
        }
    }

    override fun render(renderer: Renderer) {
        layers.forEach {
            it.render(renderer)
        }
    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        return layers.asReversed().any { it.event(event, executor) }
    }

    override fun toString(): String = "KSceneLayered($name)"
}