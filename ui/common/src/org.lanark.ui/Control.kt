package org.lanark.ui

import org.lanark.application.*
import org.lanark.events.*
import org.lanark.geometry.*

abstract class Control {
    abstract fun render(dialog: Dialog, frame: Frame)

    open fun event(dialog: Dialog, frame: Frame, event: Event): Boolean = false

    abstract fun contains(point: Point, area: Rect): Boolean
}