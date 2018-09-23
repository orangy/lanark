package org.lanark.ui

import org.lanark.drawing.*
import org.lanark.geometry.*

abstract class Control {
    abstract fun render(area: Rect, renderer: Renderer)
}