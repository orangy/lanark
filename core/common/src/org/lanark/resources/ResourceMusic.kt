package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceMusic(name: String, val location: FileLocation) : Resource<Music>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Music {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            context.engine.loadMusic(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Music")
    }
}

fun ResourceContainer.music(name: String, file: String) =
    ResourceMusic(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadMusic(path: String) = loadResource<Music>(path, ResourceMusic.resourceType)

