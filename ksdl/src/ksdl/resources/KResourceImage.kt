package ksdl.resources

import ksdl.io.*
import ksdl.rendering.*

class KResourceImage(name: String, val location: KFileLocation) : KResource<KSurface>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KSurface {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KSurface.load(file, fileSystem).also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Image")
    }
}


fun KResourceContainer.image(name: String, file: String) = KResourceImage(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadImage(path: String) = loadResource<KSurface>(path, KResourceImage.resourceType)

