package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*
import org.lanark.system.*

class ResourceCursor(name: String, val location: FileLocation, val hotX: Int, val hotY: Int) :
    ResourceDescriptor<Cursor?, Nothing?>(name, resourceType) {
    
    override fun bind(resource: Cursor?, frame: Frame): Nothing? = null

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Cursor? {
        val (path, fileSystem) = location
        return context.loadImage(path, fileSystem).use { image ->
            context.createCursor(image, hotX, hotY).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Cursor")
    }
}

fun ResourceContainer.cursor(name: String, file: String, hotX: Int, hotY: Int) =
    ResourceCursor(name, FileLocation(file, fileSystem), hotX, hotY).also { register(it) }

fun ResourceContext.cursor(path: String) = loadResource<Cursor>(path, ResourceCursor.resourceType)