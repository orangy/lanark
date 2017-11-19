package ksdl.resources

import ksdl.io.*
import ksdl.rendering.*
import ksdl.system.*

class KResourceTexture(name: String, val location: KFileLocation) : KResource<KTexture>(name, resourceType) {
    override fun load(context: KResourceContext, progress: (Double) -> Unit): KTexture {
        return context.loadIfAbsent(this) {
            val (file, fileSystem) = location
            KSurface.load(file, fileSystem).use {
                context.renderer.createTexture(it)
            }.also { progress(1.0) }
        }
    }

    companion object {
        val resourceType = KResourceType("Texture")
    }
}

fun KResourceContainer.texture(name: String, file: String) = KResourceTexture(name, KFileLocation(file, fileSystem)).also { register(it) }
fun KResourceContext.loadTexture(path: String) = loadResource<KTexture>(path, KResourceTexture.resourceType)