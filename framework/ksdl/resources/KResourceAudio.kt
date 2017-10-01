package ksdl.resources

import ksdl.system.*

class KResourceAudio(name: String, val file: String) : KResource(name, resourceType) {
    fun load(fileSystem: KFileSystem): KAudio = TODO("Load audio")
    override fun release() = TODO()

    companion object {
        val resourceType = KResourceType("Audio")
    }
}

fun KResourceScope.audio(name: String, file: String) = KResourceAudio(name, file).also { register(it) }

fun KResourceScope.loadAudio(path: String): KAudio {
    val resource = findResource(path)
    if (resource.resourceType != KResourceAudio.resourceType)
        throw KPlatformException("Resource '$resource' is not an Audio")
    return (resource as KResourceAudio).load(fileSystem)
}

