package ksdl.resources

import ksdl.diagnostics.*
import ksdl.rendering.*
import ksdl.system.*
import ksdl.io.*

class KResourceCursor(name: String, val location: KFileLocation, val hotX: Int, val hotY: Int) : KResource<KCursor>(name, resourceType) {
    private var cursor: KCursor? = null

    override fun load(progress: (Double) -> Unit): KCursor {
        cursor?.let { return it }
        val (file, fileSystem) = location
        return KSurface.load(file, fileSystem).use { surface ->
            KCursor.create(surface, hotX, hotY).also {
                cursor = it
                logger.system("Loaded $it from $this")
                progress(1.0)
            }
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

fun KResourceContainer.cursor(name: String, file: String, hotX: Int, hotY: Int) = KResourceCursor(name, KFileLocation(file, fileSystem), hotX, hotY).also { register(it) }

fun KResourceSource.loadCursor(path: String) = loadResource<KCursor>(path, KResourceCursor.resourceType)