package org.lanark.ui

import org.lanark.application.*
import org.lanark.events.*

abstract class UIScene : Scene {
    override fun activate(frame: Frame) {

    }

    override fun deactivate(frame: Frame) {

    }

    override fun event(frame: Frame, event: Event): Boolean {
        return false
    }

}

