package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*

interface Scene {
    fun activate(executor: TaskExecutor)
    fun deactivate(executor: TaskExecutor)

    fun render(frame: Frame)
    fun event(event: Event, executor: TaskExecutor): Boolean
}

