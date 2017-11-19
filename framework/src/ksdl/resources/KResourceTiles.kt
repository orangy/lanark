package ksdl.resources

import ksdl.diagnostics.*
import ksdl.rendering.*
import ksdl.system.*
import ksdl.io.*

class KResourceTiles(name: String, val location: KFileLocation, val configure: KResourceTiles.() -> Unit = emptyConfigure) : KResource<KTiles>(name, resourceType) {
    private var tiles: KTiles? = null

    override fun release() {
        tiles?.release()
        tiles = null
    }

    override fun load(progress: (Double) -> Unit): KTiles {
        tiles?.let { return it }
        val (file, fileSystem) = location
        val surface = KSurface.load(file, fileSystem)
        val tiles = KTiles(surface)
        return tiles.also {
            logger.system("Loaded $it from $this")
            progress(1.0)
        }
    }

    companion object {
        val resourceType = KResourceType("Tileset")
        val emptyConfigure: KResourceTiles.() -> Unit = {}
    }

    fun tile(name: String, x: Int, y: Int, width: Int, height: Int) {

    }
}

fun KResourceContainer.tiles(name: String, file: String, configure: KResourceTiles.() -> Unit = KResourceTiles.emptyConfigure) = KResourceTiles(name, KFileLocation(file, fileSystem)).also { register(it) }

fun KResourceSource.loadTiles(path: String) = loadResource<KTiles>(path, KResourceTiles.resourceType)


