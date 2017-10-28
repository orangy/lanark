package ksdl.resources

import ksdl.system.*

class KResourceScope(name: String = "", val fileSystem: KFileSystem = KFileSystem.Default, configure: KResourceScope.() -> Unit = emptyConfigure) : KResource(name, resourceType) {
    private val resources = mutableMapOf<String, KResource>()

    init {
        apply(configure)
    }

    fun scope(name: String, configure: KResourceScope.() -> Unit = emptyConfigure) = KResourceScope(name, fileSystem).apply(configure).also { register(it) }

    fun register(resource: KResource) {
        val existing = resources[resource.name]
        if (existing != null)
            return logger.error("Resource '${resource.name}' is already registered in '$this' for '$existing'")
        resources.put(resource.name, resource)
    }

    override fun release() {
        resources.forEach { it.value.release() }
    }

    operator fun get(path: String): KResourceScope {
        val resource = findResource(path)
        if (resource.resourceType != resourceType)
            throw KPlatformException("Resource '$resource' is not a Scope")
        return resource as KResourceScope
    }

    fun findResource(path: String, resourceType: KResourceType): KResource {
        val resource = findResource(path)
        if (resource.resourceType != resourceType)
            throw KPlatformException("Resource '$resource' is not a ${resourceType.name}")
        return resource
    }

    fun findResource(path: String): KResource {
        return path.split('/').fold<String, KResource>(this) { resource, name ->
            if (resource.resourceType != resourceType)
                throw KPlatformException("Part of the path '$name' is registered to '$resource' which is not a Scope")
            val scope = resource as KResourceScope
            scope.resources[name] ?: throw KPlatformException("Scope '$scope' doesn't contain resource '$name'")
        }
    }

    companion object {
        val resourceType = KResourceType("Scope")

        private val emptyConfigure: KResourceScope.() -> Unit = {}
    }

    fun release(path: String) {
        val resource = findResource(path)
        resource.release()
    }
}

