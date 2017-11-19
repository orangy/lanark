package ksdl.resources

import ksdl.rendering.*
import ksdl.io.*

class KResourceTiles(name: String, val location: KFileLocation, val configure: KResourceTiles.() -> Unit = emptyConfigure) : KResource<KTiles>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KTiles {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            val surface = KSurface.load(file, fileSystem)
            KTiles(surface).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Tileset")
        val emptyConfigure: KResourceTiles.() -> Unit = {}
    }

    fun tile(name: String, x: Int, y: Int, width: Int, height: Int) {

    }
}

fun KResourceContainer.tiles(name: String, file: String, configure: KResourceTiles.() -> Unit = KResourceTiles.emptyConfigure) = KResourceTiles(name, KFileLocation(file, fileSystem)).apply(configure).also { register(it) }

fun KResourceContext.loadTiles(path: String) = loadResource<KTiles>(path, KResourceTiles.resourceType)


