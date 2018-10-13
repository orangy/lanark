package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.media.*
import org.lanark.system.*

expect class Texture : Managed {
    val size: Size
}

val Texture.width: Int get() = size.width
val Texture.height: Int get() = size.height

expect fun Frame.draw(texture: Texture)
expect fun Frame.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect)
expect fun Frame.draw(texture: Texture, destinationRect: Rect)
expect fun Frame.fill(texture: Texture, destinationRect: Rect)
expect fun Frame.draw(texture: Texture, position: Point)
expect fun Frame.draw(texture: Texture, position: Point, size: Size)
expect fun Frame.bindTexture(image: Image): Texture 