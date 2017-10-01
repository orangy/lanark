package ksdl.composition

import ksdl.events.*
import ksdl.resources.*
import ksdl.system.*

interface KScene {
    fun activate(executor: KTaskExecutor)
    fun deactivate(executor: KTaskExecutor)

    fun render(renderer: KRenderer, cache: KTextureCache)
    fun keyboard(event: KEventKey, executor: KTaskExecutor)
    fun mouse(event: KEventMouse, executor: KTaskExecutor)
}

