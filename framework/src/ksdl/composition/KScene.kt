package ksdl.composition

import ksdl.events.*
import ksdl.rendering.*
import ksdl.resources.*
import ksdl.system.*

interface KScene {
    fun activate(executor: KTaskExecutor)
    fun deactivate(executor: KTaskExecutor)

    fun render(renderer: KRenderer, cache: KTextureCache)
    fun event(event: KEvent, executor: KTaskExecutor)
}

