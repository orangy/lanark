package org.lanark.drawing

import org.lanark.geometry.*
import org.lanark.system.*

class Tiles(val texture: Texture, val tiles: Map<String, Tile>) : Managed {
    override fun release() {
        texture.release()
    }

    override fun toString() = "Tiles $texture"
    operator fun get(name: String): Tile {
        return tiles[name] ?: throw Exception("Tile '$name' cannot be found")
    }
}

class Tile(val name: String, val texture: Texture, val rectangle: Rect, val origin: Point) {
    val width get() = rectangle.width
    val height get() = rectangle.height

    override fun toString() = "Tile $name $rectangle"
}

expect fun Renderer.draw(tile: Tile, position: Point)
expect fun Renderer.fill(tile: Tile, destinationRect: Rect)