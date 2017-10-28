package ksdl.resources

import ksdl.system.*

class KResourceImage(name: String, val file: String) : KResource(name, resourceType) {
    private var surface: KSurface? = null

    override fun release() {
        surface?.release()
        surface = null
    }

    fun load(fileSystem: KFileSystem): KSurface {
        surface?.let { return it }
        return KPlatform.loadSurface(file, fileSystem).also { surface = it }.also {
            logger.system("Loaded $it from $this")
        }
    }

    companion object {
        val resourceType = KResourceType("Image")
    }
}


fun KResourceScope.image(name: String, file: String) = KResourceImage(name, file).also { register(it) }

fun KResourceScope.loadImage(path: String): KSurface {
    val resource = findResource(path, KResourceImage.resourceType)
    return (resource as KResourceImage).load(fileSystem)
}

