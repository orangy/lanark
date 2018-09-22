package ksdl.resources

import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.rendering.*
import ksdl.system.*

class KResourceContainer(name: String, val fileSystem: KFileSystem = KFileSystem.Default) : KResource<KResourceContext>(name, resourceType), KResourceScope {
    private val resources = mutableMapOf<String, KResource<*>>()

    fun <TResource> register(resource: KResource<TResource>) {
        val existing = resources[resource.name]
        if (existing != null)
            return logger.error("Resource '${resource.name}' is already registered in '$this' for '$existing'")
        resources.put(resource.name, resource)
    }

    override fun findResource(path: String): KResource<*> {
        return path.split('/').fold<String, KResource<*>>(this) { resource, name ->
            if (resource.resourceType != resourceType)
                throw KPlatformException("Part of the path '$name' is registered to '$resource' which is not a Scope")
            val scope = resource as KResourceContainer
            scope.resources[name] ?: throw KPlatformException("Scope '$scope' doesn't contain resource '$name'")
        }
    }

    override fun load(context: KResourceContext, progress: (Double) -> Unit): KResourceContext {
        val values = resources.values
        val step = 1.0 / values.size
        var current = 0.0
        for (resource in values) {
            resource.load(context) { progress(current + it * step) }
            current += step
        }
        progress(1.0)
        return KResourceContext(context, this)
    }

    companion object {
        val resourceType = KResourceType("Scope")
    }
}

inline fun resources(name: String, fileSystem: KFileSystem = KFileSystem.Default, configure: KResourceContainer.() -> Unit): KResourceContainer {
    return KResourceContainer(name, fileSystem).apply(configure)
}

inline fun KResourceContainer.scope(name: String, configure: KResourceContainer.() -> Unit): KResourceContainer {
    return KResourceContainer(name, fileSystem).apply(configure).also { register(it) }
}

fun KResourceContext.loadScope(path: String) = loadResource<KResourceContext>(path, KResourceContainer.resourceType)
