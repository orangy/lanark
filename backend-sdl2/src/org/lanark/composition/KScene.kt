package org.lanark.composition

import org.lanark.events.*
import org.lanark.rendering.*
import org.lanark.system.*

interface KScene {
    fun activate(executor: KTaskExecutor)
    fun deactivate(executor: KTaskExecutor)

    fun render(renderer: Renderer)
    fun event(event: KEvent, executor: KTaskExecutor): Boolean
}

