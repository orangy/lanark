package ksdl.resources

import ksdl.diagnostics.*
import ksdl.system.*
import ksdl.io.*

class KResourceMusic(name: String, val location: KFileLocation) : KResource<KMusic>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KMusic {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KMusic.load(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Music")
    }
}

fun KResourceContainer.music(name: String, file: String) = KResourceMusic(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadMusic(path: String) = loadResource<KMusic>(path, KResourceMusic.resourceType)

