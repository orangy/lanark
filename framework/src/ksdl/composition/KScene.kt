package ksdl.composition

import ksdl.events.*
import ksdl.rendering.*
import ksdl.system.*

interface KScene {
    fun activate(executor: KTaskExecutor)
    fun deactivate(executor: KTaskExecutor)

    fun render(renderer: KRenderer)
    fun event(event: KEvent, executor: KTaskExecutor)
}

