package ksdl.resources

import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.rendering.*
import ksdl.system.*

class KResourceTiles(name: String, val file: String, val configure: KResourceTiles.() -> Unit = emptyConfigure) : KResource(name, resourceType) {
    private var tiles: KTiles? = null

    override fun release() {
        tiles?.release()
        tiles = null
    }

    fun load(fileSystem: KFileSystem): KTiles {
        tiles?.let { return it }
        val surface = KSurface.load(file, fileSystem)
        val tiles = KTiles(surface)
        return tiles.also {
            logger.system("Loaded $it from $this")
        }
    }

    companion object {
        val resourceType = KResourceType("Tileset")
        val emptyConfigure: KResourceTiles.() -> Unit = {}
    }

    fun tile(name: String, x: Int, y: Int, width: Int, height: Int) {

    }
}

fun KResourceScope.tiles(name: String, file: String, configure: KResourceTiles.() -> Unit = KResourceTiles.emptyConfigure) = KResourceTiles(name, file).also { register(it) }
fun KResourceScope.loadTiles(path: String): KTiles {
    val resource = findResource(path)
    if (resource.resourceType != KResourceImage.resourceType)
        throw KPlatformException("Resource '$resource' is not a Tileset")
    return (resource as KResourceTiles).load(fileSystem)
}

