package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*
import org.lanark.media.*

class ResourceVideo(name: String, val location: FileLocation) :
    ResourceDescriptor<Video, Nothing?>(name, resourceType) {
    override fun load(context: ResourceContext, progress: (Double) -> Unit): Video {
        val (path, fileSystem) = location
        return context.loadVideo(path, fileSystem).also { progress(1.0) }
    }

    override fun bind(resource: Video, frame: Frame): Nothing? = null

    companion object {
        val resourceType = ResourceType("Video")
    }
}

fun ResourceContainer.video(name: String, file: String) =
    ResourceVideo(name, FileLocation(file, fileSystem)).also { register(it) }

fun ResourceContext.loadVideo(path: String) = loadResource<Video>(path, ResourceVideo.resourceType)

