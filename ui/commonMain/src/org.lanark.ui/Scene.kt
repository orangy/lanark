package org.lanark.ui

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.events.*

interface Scene {
    fun activate(frame: Frame)
    fun deactivate(frame: Frame)

    fun render(frame: Frame)
    fun event(frame: Frame, event: Event): Boolean
}

