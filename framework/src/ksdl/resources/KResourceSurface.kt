package ksdl.resources

import ksdl.diagnostics.*
import ksdl.rendering.*
import ksdl.system.*
import ksdl.io.*

class KResourceSurface(name: String, val location: KFileLocation) : KResource<KSurface>(name, resourceType) {
    private var surface: KSurface? = null

    override fun release() {
        surface?.release()
        surface = null
    }

    override fun load(progress: (Double) -> Unit): KSurface {
        surface?.let { return it }
        val (file, fileSystem) = location
        return KSurface.load(file, fileSystem).also {
            surface = it
            logger.system("Loaded $it from $this")
            progress(1.0)
        }
    }

    companion object {
        val resourceType = KResourceType("Image")
    }
}


fun KResourceContainer.image(name: String, file: String) = KResourceSurface(name, KFileLocation(file, fileSystem)).also { register(it) }

fun KResourceSource.loadImage(path: String) = loadResource<KSurface>(path, KResourceSurface.resourceType)

