package org.lanark.resources

import org.lanark.application.*
import org.lanark.system.*

class ResourceContext(val frame: Frame, val resources: ResourceContainer) : Managed {
    val logger get() = frame.engine.logger

    private val loadedResources = mutableMapOf<ResourceDescriptor<*, *>, Any?>()
    private val boundResources = mutableMapOf<ResourceDescriptor<*, *>, Any?>()

    fun <TResource> loadResource(path: String, resourceType: ResourceType): TResource {
        val descriptor = resources.findResource<TResource>(path, resourceType)
        return loadIfAbsent(descriptor) {
            descriptor.load(this, emptyProgress)
        } as TResource
    }

    fun <TBound, TResource> bindResource(path: String, resourceType: ResourceType): TBound {
        val descriptor = resources.findResource<TResource>(path, resourceType) as? ResourceDescriptor<TResource, TBound>
            ?: throw ResourcesException("Attempt to bind non-bindable descriptor")

        return boundResources.getOrPut(descriptor) {
            val resource = loadResource<TResource>(path, resourceType)
            descriptor.bind(resource, frame)
        } as TBound
    }

    override fun release() {
        boundResources.forEach { (it.value as? Managed)?.release() }
        boundResources.clear()

        loadedResources.forEach { (it.value as? Managed)?.release() }
        loadedResources.clear()
    }

    fun loadIfAbsent(
        resource: ResourceDescriptor<*, *>,
        loader: () -> Any?
    ): Any? {
        @Suppress("UNCHECKED_CAST")
        return loadedResources.getOrPut(resource, loader)
    }

    companion object {
        private val emptyProgress: (Double) -> Unit = {}
    }
}

