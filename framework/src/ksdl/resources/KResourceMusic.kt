package ksdl.resources

import ksdl.io.*
import ksdl.system.*

class KResourceMusic(name: String, val file: String) : KResource(name, resourceType) {
    private var music: KMusic? = null
    fun load(fileSystem: KFileSystem): KMusic {
        music?.let { return it }
        return KPlatform.loadMusic(file, fileSystem).also { music = it }.also {
            logger.system("Loaded $it from $this")
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

fun KResourceScope.music(name: String, file: String) = KResourceMusic(name, file).also { register(it) }

fun KResourceScope.loadMusic(path: String): KMusic {
    val resource = findResource(path, KResourceMusic.resourceType)
    return (resource as KResourceMusic).load(fileSystem)
}

