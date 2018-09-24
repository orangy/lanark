package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.resources.*
import org.lanark.system.*

actual class Renderer : ResourceOwner, Managed {
    override fun release() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual val frame: Frame
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual var size: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}
    actual var clip: Rect?
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun clear(color: Color?) {}
    actual fun color(color: Color) {}
    actual fun scale(scale: Float) {}
    actual fun drawLine(from: Point, to: Point) {}
    actual fun present() {}

}