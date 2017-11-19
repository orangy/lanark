package ksdl.resources

import ksdl.diagnostics.*
import ksdl.io.*
import ksdl.system.*

class KResourceContainer(name: String = "", val fileSystem: KFileSystem = KFileSystem.Default) : KResource<KResourceSource>(name, resourceType), KResourceSource {
    private val resources = mutableMapOf<String, KResource<*>>()

    fun scope(name: String, configure: KResourceContainer.() -> Unit = emptyConfigure): KResourceContainer {
        return KResourceContainer(name, fileSystem).apply(configure).also { register(it) }
    }

    fun <TResource> register(resource: KResource<TResource>) {
        val existing = resources[resource.name]
        if (existing != null)
            return logger.error("Resource '${resource.name}' is already registered in '$this' for '$existing'")
        resources.put(resource.name, resource)
    }

    override fun release() {
        resources.forEach { it.value.release() }
    }

    override fun findResource(path: String): KResource<*> {
        return path.split('/').fold<String, KResource<*>>(this) { resource, name ->
            if (resource.resourceType != resourceType)
                throw KPlatformException("Part of the path '$name' is registered to '$resource' which is not a Scope")
            val scope = resource as KResourceContainer
            scope.resources[name] ?: throw KPlatformException("Scope '$scope' doesn't contain resource '$name'")
        }
    }

    override fun load(progress: (Double) -> Unit): KResourceSource {
        val values = resources.values
        val step = 1.0 / values.size
        var current = 0.0
        for (resource in values) {
            resource.load { progress(current + it * step) }
            current += step
        }
        progress(1.0)
        return this
    }

    companion object {
        val resourceType = KResourceType("Scope")

        private val emptyConfigure: KResourceContainer.() -> Unit = {}
    }
}

fun resources(name: String = "", fileSystem: KFileSystem = KFileSystem.Default, configure: KResourceContainer.() -> Unit): KResourceContainer {
    return KResourceContainer(name, fileSystem).apply(configure)
}