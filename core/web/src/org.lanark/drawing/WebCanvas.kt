package org.lanark.drawing

import org.lanark.geometry.*
import org.lanark.system.*

actual class Canvas : Managed {
    override fun release() {
    }

    actual val size: Size
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    actual var blendMode: BlendMode
        get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        set(value) {}

    actual fun blit(source: Canvas) {}
    actual fun blit(source: Canvas, sourceRect: Rect, destination: Point) {}
    actual fun blitScaled(source: Canvas) {}
    actual fun blitScaled(source: Canvas, sourceRect: Rect, destinationRect: Rect) {}
    actual fun fill(color: Color) {}
    actual fun fill(color: Color, rectangle: Rect) {}

}