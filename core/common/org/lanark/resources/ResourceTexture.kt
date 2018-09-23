package org.lanark.resources

import org.lanark.application.*
import org.lanark.drawing.*
import org.lanark.io.*

class ResourceTexture(name: String, val location: FileLocation) : Resource<Texture>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Texture {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            (context.owner as Renderer).loadTexture(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Texture")
    }
}

fun ResourceContainer.texture(name: String, file: String) = ResourceTexture(name, FileLocation(file, fileSystem)).also { register(it) }
fun ResourceContext.loadTexture(path: String) = loadResource<Texture>(path, ResourceTexture.resourceType)