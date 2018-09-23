package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceSound(name: String, val location: FileLocation) : Resource<Sound>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Sound {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            context.engine.loadSound(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Sound")
    }
}

fun ResourceContainer.sound(name: String, file: String) = ResourceSound(name, FileLocation(file, fileSystem)).also { register(it) }
fun ResourceContext.loadSound(path: String) = loadResource<Sound>(path, ResourceSound.resourceType)