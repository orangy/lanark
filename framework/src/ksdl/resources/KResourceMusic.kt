package ksdl.resources

import ksdl.diagnostics.*
import ksdl.system.*
import ksdl.io.*

class KResourceMusic(name: String, val location: KFileLocation) : KResource<KMusic>(name, resourceType) {
    private var music: KMusic? = null
    override fun load(progress: (Double) -> Unit): KMusic {
        music?.let { return it }
        val (file, fileSystem) = location
        return KMusic.load(file, fileSystem).also { music = it }.also {
            logger.system("Loaded $it from $this")
            progress(1.0)
        }
    }

    override fun release() {
        music?.release()
        music = null
    }

    companion object {
        val resourceType = KResourceType("Music")
    }
}

fun KResourceContainer.music(name: String, file: String) = KResourceMusic(name, KFileLocation(file, fileSystem)).also { register(it) }

fun KResourceSource.loadMusic(path: String) = loadResource<KMusic>(path, KResourceMusic.resourceType)

