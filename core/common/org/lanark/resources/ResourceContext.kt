package org.lanark.resources

import org.lanark.application.*
import org.lanark.system.*

class ResourceContext private constructor(
    val engine: Engine,
    val owner: ResourceOwner,
    private val resources: ResourceScope,
    private val cache: MutableMap<Resource<*>, Managed>
) : Managed {

    constructor(engine: Engine, owner: ResourceOwner, resources: ResourceScope) : this(engine, owner, resources, mutableMapOf())
    constructor(context: ResourceContext, resources: ResourceScope) : this(context.engine, context.owner, resources, context.cache)

    fun <TResource> loadResource(path: String, resourceType: ResourceType): TResource {
        return resources.findResource<TResource>(path, resourceType).load(this, emptyProgress)
    }

    override fun release() {
        cache.forEach { it.value.release() }
        cache.clear()
    }

    fun <TResource : Managed> loadIfAbsent(resource: Resource<TResource>, loader: () -> TResource): TResource {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(resource, loader) as TResource
    }

    companion object {
        private val emptyProgress: (Double) -> Unit = {}
    }
}

fun ResourceScope.bind(frame: Frame) = ResourceContext(frame.engine, frame.renderer, this)

