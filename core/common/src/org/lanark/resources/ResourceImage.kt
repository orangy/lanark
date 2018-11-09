package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceImage(name: String, val location: FileLocation) :
    ResourceDescriptor<Image, Nothing?>(name, resourceType) {
    
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Image {
        val (path, fileSystem) = location
        return context.loadImage(path, fileSystem).also { progress(1.0) }
    }

    override fun bind(resource: Image, frame: Frame): Nothing? = null

    companion object {
        val resourceType = ResourceType("Image")
    }
}

fun ResourceContainer.image(name: String, file: String) =
    ResourceImage(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadImage(path: String) = loadResource<Image>(path, ResourceImage.resourceType)

