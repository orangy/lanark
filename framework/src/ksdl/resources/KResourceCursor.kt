package ksdl.resources

import ksdl.io.*
import ksdl.system.*

class KResourceCursor(name: String, val file: String, val hotX: Int, val hotY: Int) : KResource(name, resourceType) {
    private var cursor: KCursor? = null
    fun load(fileSystem: KFileSystem): KCursor {
        cursor?.let { return it }
        val surface = KPlatform.loadSurface(file, fileSystem)
        return KPlatform.createCursor(surface, hotX, hotY).also { cursor = it }.also {
            surface.release()
            logger.system("Loaded $it from $this")
        }
    }

    override fun release() {
        cursor?.release()
        cursor = null
    }

    companion object {
        val resourceType = KResourceType("Cursor")
    }
}

fun KResourceScope.cursor(name: String, file: String, hotX: Int, hotY: Int) = KResourceCursor(name, file, hotX, hotY).also { register(it) }
fun KResourceScope.loadCursor(path: String): KCursor {
    val resource = findResource(path, KResourceCursor.resourceType)
    return (resource as KResourceCursor).load(fileSystem)
}