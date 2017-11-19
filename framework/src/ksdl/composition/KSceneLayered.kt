package ksdl.composition

import ksdl.events.*
import ksdl.rendering.*
import ksdl.system.*

class KSceneLayered(val layers: List<KScene>) : KScene {
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

    override fun render(renderer: KRenderer) {
        layers.forEach {
            it.render(renderer)
        }
    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        return layers.asReversed().any { it.event(event, executor) }
    }
}