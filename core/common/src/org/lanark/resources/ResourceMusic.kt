package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceMusic(name: String, val location: FileLocation) :
    ResourceDescriptor<Music, Nothing?>(name, resourceType) {
    
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Music {
        val (path, fileSystem) = location
        return context.loadMusic(path, fileSystem).also { progress(1.0) }
    }

    override fun bind(resource: Music, frame: Frame): Nothing? = null

    companion object {
        val resourceType = ResourceType("Music")
    }
}

fun ResourceContainer.music(name: String, file: String) =
    ResourceMusic(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadMusic(path: String) = loadResource<Music>(path, ResourceMusic.resourceType)

