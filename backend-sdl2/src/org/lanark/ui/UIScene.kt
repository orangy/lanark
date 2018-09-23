package org.lanark.ui

import org.lanark.composition.*
import org.lanark.events.*
import org.lanark.system.*

abstract class UIScene : KScene {
    override fun activate(executor: KTaskExecutor) {

    }

    override fun deactivate(executor: KTaskExecutor) {

    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        return false
    }

}

