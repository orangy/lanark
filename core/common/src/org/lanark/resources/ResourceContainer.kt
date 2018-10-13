package org.lanark.resources

import org.lanark.application.*
import org.lanark.io.*

class ResourceContainer(name: String, val fileSystem: FileSystem = FileSystems.Default) :
    ResourceDescriptor<ResourceContext, Nothing?>(name, resourceType) {

    private val resources = mutableMapOf<String, ResourceDescriptor<*, *>>()

    fun <TResource> register(resource: ResourceDescriptor<TResource, *>, name: String = resource.name) {
        val existing = resources[name]
        if (existing != null)
            throw ResourcesException("Resource '$name' is already registered in '$this' for '$existing'")
        resources[name] = resource
    }

    fun findResource(path: String): ResourceDescriptor<*, *> {
        return path.split('/').fold<String, ResourceDescriptor<*, *>>(this) { resource, name ->
            if (resource.resourceType != ResourceContainer.resourceType)
                throw ResourcesException("Part of the path '$name' is registered to '$resource' which is not a Container")
            val scope = resource as ResourceContainer
            scope.resources[name] ?: throw ResourcesException("Container '$scope' doesn't contain resource '$name'")
        }
    }

    override fun load(context: ResourceContext, progress: (Double) -> Unit): ResourceContext {
        return load(context.frame, progress)
    }

    override fun bind(resource: ResourceContext, frame: Frame): Nothing? = null

    fun load(frame: Frame, progress: (Double) -> Unit): ResourceContext {
        val nestedContext = ResourceContext(frame, this)
        val values = resources.values
        val step = 1.0 / values.size
        var current = 0.0
        for (resource in values) {
            nestedContext.loadIfAbsent(resource) {
                resource.load(nestedContext) { progress(current + it * step) }
            }
            current += step
        }
        progress(1.0)
        return nestedContext
    }

    companion object {
        val resourceType = ResourceType("Container")
    }
}

inline fun scope(
    name: String, fileSystem: FileSystem = FileSystems.Default,
    configure: ResourceContainer.() -> Unit
): ResourceContainer {
    return ResourceContainer(name, fileSystem).apply(configure)
}

inline fun ResourceContainer.scope(
    name: String,
    fileSystem: FileSystem = this.fileSystem,
    configure: ResourceContainer.() -> Unit
): ResourceContainer {
    return ResourceContainer(name, fileSystem).apply(configure).also { register(it) }
}

fun ResourceContext.loadScope(path: String) = loadResource<ResourceContext>(path, ResourceContainer.resourceType)

fun <TResource> ResourceContainer.findResource(
    path: String,
    resourceType: ResourceType
): ResourceDescriptor<TResource, *> {
    val resource = findResource(path)
    if (resource.resourceType != resourceType)
        throw ResourcesException("Resource '$resource' is not a ${resourceType.name}")

    @Suppress("UNCHECKED_CAST")
    return resource as ResourceDescriptor<TResource, *>
}
