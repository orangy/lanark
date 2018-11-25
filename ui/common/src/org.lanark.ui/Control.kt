package org.lanark.ui

import org.lanark.application.*
import org.lanark.geometry.*

abstract class Control {
    abstract fun render(frame: Frame, area: Rect)
    
    abstract fun contains(point: Point, area: Rect): Boolean
}