package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceVideo(name: String, val location: FileLocation) : Resource<Video>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Video {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            context.engine.loadVideo(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = ResourceType("Video")
    }
}

fun ResourceContainer.video(name: String, file: String) =
    ResourceVideo(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadVideo(path: String) = loadResource<Video>(path, ResourceVideo.resourceType)

