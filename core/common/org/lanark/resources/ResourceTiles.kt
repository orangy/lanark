package org.lanark.resources

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.geometry.*
import org.lanark.io.*

class ResourceTiles(name: String, val location: FileLocation) : Resource<Tiles>(name, resourceType) {
    private val items = mutableMapOf<String, Item>()

    private class Item(val x: Int, val y: Int, val width: Int, val height: Int, val hotX: Int, val hotY: Int)

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Tiles {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            val texture : Texture = (context.owner as Renderer).loadTexture(file, fileSystem)
            val tiles = items.mapValues { (name, item) ->
                Tile(name, texture, Rect(item.x, item.y, item.width, item.height), Point(item.hotX, item.hotY))
            }
            Tiles(texture, tiles).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Tiles")
    }

    fun tile(name: String, x: Int, y: Int, width: Int, height: Int, hotX: Int = 0, hotY: Int = 0) {
        items.put(name, Item(x, y, width, height, hotX, hotY))
    }

    fun tile(name: String, rectangle: Rect, hotPoint: Point = Point.Zero) {
        items.put(name, Item(rectangle.x, rectangle.y, rectangle.width, rectangle.height, hotPoint.x, hotPoint.y))
    }
}

fun ResourceContainer.tiles(name: String, file: String, configure: ResourceTiles.() -> Unit) = ResourceTiles(name, FileLocation(file, fileSystem)).apply(configure).also { register(it) }

fun ResourceContext.loadTiles(path: String) = loadResource<Tiles>(path, ResourceTiles.resourceType)


