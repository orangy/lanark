package ksdl.resources

import ksdl.io.*

class KResourceVideo(name: String, val file: String) : KResource(name, resourceType) {
    fun load(fileSystem: KFileSystem): KVideo = TODO("Load video")
    override fun release() = TODO()

    companion object {
        val resourceType = KResourceType("Video")
    }
}

fun KResourceScope.video(name: String, file: String) = KResourceVideo(name, file).also { register(it) }
fun KResourceScope.loadVideo(path: String): KVideo =
        (findResource(path, KResourceVideo.resourceType) as KResourceVideo).load(fileSystem)

