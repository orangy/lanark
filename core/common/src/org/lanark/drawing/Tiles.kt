package org.lanark.drawing

import org.lanark.application.*
import org.lanark.geometry.*
import org.lanark.system.*

class Tiles(val texture: Texture, private val tiles: Map<String, Tile>) : Managed {
    override fun release() {
        texture.release()
    }

    override fun toString() = "Tiles $texture"
    operator fun get(name: String): Tile {
        return tiles[name] ?: throw Exception("Tile '$name' cannot be found")
    }
}

class Tile(val name: String, val texture: Texture, val rectangle: Rect, val origin: Point = Point.Zero) {
    val size get() = rectangle.size
    val width get() = rectangle.width
    val height get() = rectangle.height

    override fun toString() = "Tile $name $rectangle"
}

fun Frame.draw(tile: Tile, position: Point) {
    val srcRect = tile.rectangle
    val dstRect = Rect(position.x - tile.origin.x, position.y - tile.origin.y, tile.width, tile.height)
    draw(tile.texture, srcRect, dstRect)
}

fun Frame.fill(tile: Tile, destinationRect: Rect) = fill(tile.texture, tile.rectangle, destinationRect)