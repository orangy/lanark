package org.lanark.drawing

import org.lanark.geometry.*
import org.lanark.io.*
import org.lanark.system.*

expect class Texture : Managed {
    val size: Size
}

val Texture.width: Int get() = size.width
val Texture.height: Int get() = size.height

expect fun Renderer.draw(texture: Texture)
expect fun Renderer.draw(texture: Texture, sourceRect: Rect, destinationRect: Rect)
expect fun Renderer.draw(texture: Texture, destinationRect: Rect)
expect fun Renderer.fill(texture: Texture, destinationRect: Rect)
expect fun Renderer.draw(texture: Texture, position: Point)
expect fun Renderer.draw(texture: Texture, position: Point, size: Size)
expect fun Renderer.loadTexture(path: String, fileSystem: FileSystem): Texture 

