package ksdl.resources

import ksdl.io.*
import ksdl.media.*

class KResourceSound(name: String, val location: KFileLocation) : KResource<KSound>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KSound {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KSound.load(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Sound")
    }
}

fun KResourceContainer.sound(name: String, file: String) = KResourceSound(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadSound(path: String) = loadResource<KSound>(path, KResourceSound.resourceType)