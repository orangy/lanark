package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*

actual fun Frame.draw(tile: Tile, position: Point) {
    val srcRect = tile.rectangle
    val dstRect = Rect(position.x - tile.origin.x, position.y - tile.origin.y, tile.width, tile.height)
    draw(tile.texture, srcRect, dstRect)
}

actual fun Frame.fill(tile: Tile, destinationRect: Rect) {
    val srcRect = tile.rectangle
    clip(destinationRect) {
        for (x in destinationRect.left..destinationRect.right step tile.width) {
            for (y in destinationRect.top..destinationRect.bottom step tile.height) {
                draw(tile.texture, srcRect, destinationRect)
            }
        }
    }
}