package org.lanark.media

import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.resources.*
import org.lanark.system.*

expect class Image : Managed {
    val size: Size
    var blendMode: BlendMode

    fun blit(source: Image)
    fun blit(source: Image, sourceRect: Rect, destination: Point)
    fun blitScaled(source: Image)
    fun blitScaled(source: Image, sourceRect: Rect, destinationRect: Rect)
    fun fill(color: Color)
    fun fill(color: Color, rectangle: Rect)
}

enum class BlendMode {
    None, Blend, Add, Mod
}


expect fun ResourceContext.createImage(size: Size, bitsPerPixel: Int): Image
expect fun ResourceContext.loadImage(path: String, fileSystem: FileSystem): Image 
