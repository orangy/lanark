package org.lanark.resources

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceTexture(name: String, val location: FileLocation) :
    ResourceDescriptor<Image, Texture>(name, resourceType) {
    
    override fun bind(resource: Image, frame: Frame): Texture {
        return frame.bindTexture(resource)
    }

    override fun load(context: ResourceContext, progress: (Double) -> Unit): Image {
        val (file, fileSystem) = location
        return context.loadImage(file, fileSystem).also { progress(1.0) }
    }

    companion object {
        val resourceType = ResourceType("Texture")
    }
}

fun ResourceContainer.texture(name: String, file: String) =
    ResourceTexture(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.texture(path: String) = bindResource<Texture, Image>(path, ResourceTexture.resourceType)

