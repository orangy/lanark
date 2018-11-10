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
expect fun Frame.draw(texture: Texture, position: Point)
expect fun Frame.draw(texture: Texture, position: Point, size: Size)
expect fun Frame.bindTexture(image: Image): Texture

fun Frame.fill(texture: Texture, destinationRect: Rect) {
    fill(texture, Rect(Point.Zero, texture.size), destinationRect)
}

fun Frame.fill(texture: Texture, sourceRect: Rect, destinationRect: Rect) {
    val (txWidth, txHeight) = sourceRect.size
    val right = destinationRect.right
    val bottom = destinationRect.bottom
    val left = destinationRect.left
    val top = destinationRect.top

    // Fill where texture fits as a whole
    val wholeWidth = (destinationRect.width / txWidth) * txWidth
    val wholeHeight = (destinationRect.height / txHeight) * txHeight
    val wholeRight = left + wholeWidth
    val wholeBottom = top + wholeHeight
    for (x in left until wholeRight step txWidth)
        for (y in top until wholeBottom step txHeight) {
            draw(texture, sourceRect, Rect(x, y, txWidth, txHeight))
        }

    // Fill right side, where texture is cut from right
    val rightSourceRect = Rect(sourceRect.origin, Size(right - wholeRight, txHeight))
    for (y in top until wholeBottom step txHeight) 
        draw(texture, rightSourceRect, Rect(wholeRight, y, right - wholeRight, txHeight))
    
    // Fill bottom side, where texture is cut from bottom
    val bottomSourceRect = Rect(sourceRect.origin, Size(txWidth, bottom - wholeBottom))
    for (x in left until wholeRight step txWidth)
        draw(texture, bottomSourceRect, Rect(x, wholeBottom, txWidth, bottom - wholeBottom))

    // Fill corner, where texture is cut from right & bottom
    val cornerSourceRect = Rect(sourceRect.origin, Size(right - wholeRight, bottom - wholeBottom))
    draw(texture, cornerSourceRect, Rect(wholeRight, wholeBottom, cornerSourceRect.width, cornerSourceRect.height))
}
