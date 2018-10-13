package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.system.*

class ResourceCursor(name: String, val location: FileLocation, val hotX: Int, val hotY: Int) :
    Resource<Cursor?>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Cursor? {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            context.engine.loadCanvas(file, fileSystem).use { canvas ->
                context.engine.createCursor(canvas, hotX, hotY).also { progress(1.0) }
            }
        }
    }

    companion object {
        val resourceType = ResourceType("Cursor")
    }
}

fun ResourceContainer.cursor(name: String, file: String, hotX: Int, hotY: Int) =
    ResourceCursor(name, FileLocation(file, fileSystem), hotX, hotY).also { register(it) }

fun ResourceContext.loadCursor(path: String) = loadResource<Cursor>(path, ResourceCursor.resourceType)