package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceSound(name: String, val location: FileLocation) :
    ResourceDescriptor<Sound, Nothing?>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Sound {
        val (path, fileSystem) = location
        return context.loadSound(path, fileSystem).also { progress(1.0) }
    }

    override fun bind(resource: Sound, frame: Frame): Nothing? = null

    companion object {
        val resourceType = ResourceType("Sound")
    }
}

fun ResourceContainer.sound(name: String, file: String) =
    ResourceSound(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadSound(path: String) = loadResource<Sound>(path, ResourceSound.resourceType)