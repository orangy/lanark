package ksdl.ui

import ksdl.composition.*
import ksdl.events.*
import ksdl.system.*

abstract class UIScene : KScene {
    override fun activate(executor: KTaskExecutor) {

    }

    override fun deactivate(executor: KTaskExecutor) {

    }

    override fun event(event: KEvent, executor: KTaskExecutor): Boolean {
        return false
    }

}

