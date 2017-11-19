package ksdl.resources

import ksdl.rendering.*
import ksdl.system.*

class KResourceContext private constructor(
        val renderer: KRenderer,
        private val resources: KResourceScope,
        private val cache: MutableMap<KResource<*>, KManaged>) : KManaged {

    constructor(renderer: KRenderer, resources: KResourceScope) : this(renderer, resources, mutableMapOf())
    constructor(context: KResourceContext, resources: KResourceScope) : this(context.renderer, resources, context.cache)

    fun <TResource> KResourceContext.loadResource(path: String, resourceType: KResourceType): TResource {
        return resources.findResource<TResource>(path, resourceType).load(this, emptyProgress)
    }

    override fun release() {
        cache.forEach { it.value.release() }
        cache.clear()
    }

    fun <TResource : KManaged> loadIfAbsent(resource: KResource<TResource>, loader: () -> TResource): TResource {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(resource, loader) as TResource
    }


    companion object {
        private val emptyProgress: (Double) -> Unit = {}
    }
}

fun KResourceScope.bind(renderer: KRenderer) = KResourceContext(renderer, this)

