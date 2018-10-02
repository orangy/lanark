package org.lanark.resources

import org.lanark.drawing.*
import org.lanark.io.*

class ResourceImage(name: String, val location: FileLocation) : Resource<Canvas>(name, resourceType) {
    override fun load(
        context: ResourceContext,
        progress: (Double) -> Unit
    ): Canvas {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            context.engine.loadCanvas(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Image")
    }
}


fun ResourceContainer.image(name: String, file: String) =
    ResourceImage(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadImage(path: String) = loadResource<Canvas>(path, ResourceImage.resourceType)

