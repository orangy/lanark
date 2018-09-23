package org.lanark.ui

import org.lanark.application.*
import org.lanark.events.*

abstract class UIScene : Scene {
    override fun activate(executor: TaskExecutor) {

    }

    override fun deactivate(executor: TaskExecutor) {

    }

    override fun event(event: Event, executor: TaskExecutor): Boolean {
        return false
    }

}

