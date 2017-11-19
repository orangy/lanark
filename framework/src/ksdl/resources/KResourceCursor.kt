package ksdl.resources

import ksdl.diagnostics.*
import ksdl.rendering.*
import ksdl.system.*
import ksdl.io.*

class KResourceCursor(name: String, val location: KFileLocation, val hotX: Int, val hotY: Int) : KResource<KCursor>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KCursor {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KSurface.load(file, fileSystem).use { surface ->
                KCursor.create(surface, hotX, hotY).also { progress(1.0) }
            }
        }
    }

    companion object {
        val resourceType = KResourceType("Cursor")
    }
}

fun KResourceContainer.cursor(name: String, file: String, hotX: Int, hotY: Int) = KResourceCursor(name, KFileLocation(file, fileSystem), hotX, hotY).also { register(it) }
fun KResourceContext.loadCursor(path: String) = loadResource<KCursor>(path, KResourceCursor.resourceType)