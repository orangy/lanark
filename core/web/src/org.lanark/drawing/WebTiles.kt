package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*

actual fun Frame.draw(tile: Tile, position: Point) {
    val srcRect = tile.rectangle
    val dstRect = Rect(position.x - tile.origin.x, position.y - tile.origin.y, tile.width, tile.height)
    draw(tile.texture, srcRect, dstRect)
}

actual fun Frame.fill(tile: Tile, destinationRect: Rect) {
    draw(tile.texture, tile.rectangle, destinationRect)
}