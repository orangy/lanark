package ksdl.resources

import ksdl.system.*

class KResourceScope(name: String = "", configure: KResourceScope.() -> Unit = emptyConfigure) : KResource(name, KResourceType.Scope) {
    private val resources = mutableMapOf<String, KResource>()

    init {
        apply(configure)
    }

    private fun register(resource: KResource) {
        if (resources.containsKey(resource.name))
            return logger.error("Resource ${resource.name} is already registered in $this")
        resources.put(resource.name, resource)
    }

    fun scope(name: String, configure: KResourceScope.() -> Unit = emptyConfigure) = KResourceScope(name).apply(configure).also { register(it) }

    fun image(name: String, file: String) = KResourceImage(name, file).also { register(it) }
    fun audio(name: String, file: String) = KResourceAudio(name, file).also { register(it) }
    fun video(name: String, file: String) = KResourceVideo(name, file).also { register(it) }

    fun getImage(name: String): KSurface {
        val resource = findResource(name)
        if (resource.resourceType != KResourceType.Image)
            throw KGraphicsException("Resource $resource is not an Image")
        return (resource as KResourceImage).load()
    }

    private fun findResource(path: String): KResource {
        val resource = path.split('/').fold<String, KResource>(this) { resource, name ->
            if (resource.resourceType != KResourceType.Scope)
                throw KGraphicsException("Resource $resource is not a Scope")
            val scope = resource as KResourceScope
            scope.resources[name] ?: throw KGraphicsException("Scope $scope doesn't contain resource $name")
        }
        logger.system("Found $resource for path $path")
        return resource
    }

    companion object {
        private val emptyConfigure: KResourceScope.() -> Unit = {}
    }
}

abstract class KResource(val name: String, val resourceType: KResourceType) {
    override fun toString() = "$resourceType($name)"
}

enum class KResourceType {
    Image, Audio, Video, Scope
}

class KResourceImage(name: String, val file: String) : KResource(name, KResourceType.Image) {
    private val surface by lazy { KPlatform.loadSurface(file) }

    fun load(): KSurface = surface
}

class KResourceAudio(name: String, val file: String) : KResource(name, KResourceType.Audio)
class KResourceVideo(name: String, val file: String) : KResource(name, KResourceType.Video)