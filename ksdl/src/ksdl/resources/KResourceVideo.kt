package ksdl.resources

import ksdl.io.*
import ksdl.media.*

class KResourceVideo(name: String, val location: KFileLocation) : KResource<KVideo>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KVideo {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KVideo.load(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Video")
    }
}

fun KResourceContainer.video(name: String, file: String) = KResourceVideo(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadVideo(path: String) = loadResource<KVideo>(path, KResourceVideo.resourceType)

