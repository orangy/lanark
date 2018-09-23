package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Renderer : ResourceOwner, Managed {
    val frame: Frame

    var size: Size
    var clip: Rect?

    fun clear(color: Color? = null)
    fun color(color: Color)
    fun scale(scale: Float)

    fun drawLine(from: Point, to: Point)
    
    fun present()
}

inline fun Renderer.clip(rectangle: Rect, body: () -> Unit) {
    val old = clip
    try {
        clip = rectangle
        body()
    } finally {
        clip = old
    }
}