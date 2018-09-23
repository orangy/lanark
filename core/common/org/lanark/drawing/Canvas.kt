package org.lanark.drawing

import org.lanark.geometry.*
import org.lanark.system.*

expect class Canvas : Managed {
    val size: Size
    var blendMode: BlendMode

    fun blit(source: Canvas)
    fun blit(source: Canvas, sourceRect: Rect, destination: Point)
    fun blitScaled(source: Canvas)
    fun blitScaled(source: Canvas, sourceRect: Rect, destinationRect: Rect)
    fun fill(color: Color)
    fun fill(color: Color, rectangle: Rect)
}

enum class BlendMode {
    None, Blend, Add, Mod
}
