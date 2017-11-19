package ksdl.resources

import ksdl.geometry.*
import ksdl.rendering.*
import ksdl.io.*
import ksdl.rendering.KTiles

class KResourceTiles(name: String, val location: KFileLocation) : KResource<KTiles>(name, resourceType) {
    private val items = mutableMapOf<String, Item>()

    private class Item(val x: Int, val y: Int, val width: Int, val height: Int, val hotX: Int, val hotY: Int)

    override fun load(context: KResourceContext, progress: (Double) -> Unit): KTiles {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            val texture = KTexture.load(file, fileSystem, context.renderer)
            val tiles = items.mapValues { (name, item) ->
                KTile(name, texture, KRect(item.x, item.y, item.width, item.height), KPoint(item.hotX, item.hotY))
            }
            KTiles(texture, tiles).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Tiles")
    }

    fun tile(name: String, x: Int, y: Int, width: Int, height: Int, hotX: Int = 0, hotY: Int = 0) {
        items.put(name, Item(x, y, width, height, hotX, hotY))
    }

    fun tile(name: String, rectangle: KRect, hotPoint: KPoint = KPoint.Zero) {
        items.put(name, Item(rectangle.x, rectangle.y, rectangle.width, rectangle.height, hotPoint.x, hotPoint.y))
    }
}

fun KResourceContainer.tiles(name: String, file: String, configure: KResourceTiles.() -> Unit) = KResourceTiles(name, KFileLocation(file, fileSystem)).apply(configure).also { register(it) }

fun KResourceContext.loadTiles(path: String) = loadResource<KTiles>(path, KResourceTiles.resourceType)


