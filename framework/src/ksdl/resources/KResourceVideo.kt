package ksdl.resources

import ksdl.io.*

class KResourceVideo(name: String, val location: KFileLocation) : KResource<KVideo>(name, resourceType) {
    override fun load(progress: (Double) -> Unit): KVideo {
        val (file, fileSystem) = location
        progress(1.0)
        return KVideo()
    }

    override fun release() = TODO()

    companion object {
        val resourceType = KResourceType("Video")
    }
}

fun KResourceContainer.video(name: String, file: String) = KResourceVideo(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceSource.loadVideo(path: String) = loadResource<KVideo>(path, KResourceVideo.resourceType)

