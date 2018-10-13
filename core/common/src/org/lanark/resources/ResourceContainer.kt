package org.lanark.resources

import org.lanark.io.*

class ResourceContainer(name: String, val fileSystem: FileSystem = FileSystems.Default) :
    Resource<ResourceContext>(name, resourceType), ResourceScope {
    private val resources = mutableMapOf<String, Resource<*>>()

    fun <TResource> register(resource: Resource<TResource>) {
        val existing = resources[resource.name]
        if (existing != null)
            throw ResourcesException("Resource '${resource.name}' is already registered in '$this' for '$existing'")
        resources[resource.name] = resource
    }

    override fun findResource(path: String): Resource<*> {
        return path.split('/').fold<String, Resource<*>>(this) { resource, name ->
            if (resource.resourceType != resourceType)
                throw ResourcesException("Part of the path '$name' is registered to '$resource' which is not a Scope")
            val scope = resource as ResourceContainer
            scope.resources[name] ?: throw ResourcesException("Scope '$scope' doesn't contain resource '$name'")
        }
    }

    override fun load(context: ResourceContext, progress: (Double) -> Unit): ResourceContext {
        val values = resources.values
        val step = 1.0 / values.size
        var current = 0.0
        for (resource in values) {
            resource.load(context) { progress(current + it * step) }
            current += step
        }
        progress(1.0)
        return ResourceContext(context, this)
    }

    companion object {
        val resourceType = ResourceType("Scope")
    }
}

inline fun resources(
    name: String,
    fileSystem: FileSystem = FileSystems.Default,
    configure: ResourceContainer.() -> Unit
): ResourceContainer {
    return ResourceContainer(name, fileSystem).apply(configure)
}

inline fun ResourceContainer.scope(name: String, configure: ResourceContainer.() -> Unit): ResourceContainer {
    return ResourceContainer(name, fileSystem).apply(configure).also { register(it) }
}

fun ResourceContext.loadScope(path: String) = loadResource<ResourceContext>(path, ResourceContainer.resourceType)
