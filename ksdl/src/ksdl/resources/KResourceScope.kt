package ksdl.resources

import ksdl.diagnostics.*
import ksdl.rendering.*
import ksdl.system.*

interface KResourceScope {
    fun findResource(path: String): KResource<*>
}

fun <TResource> KResourceScope.findResource(path: String, resourceType: KResourceType): KResource<TResource> {
    val resource = findResource(path)
    if (resource.resourceType != resourceType)
        throw KPlatformException("Resource '$resource' is not a ${resourceType.name}")

    @Suppress("UNCHECKED_CAST")
    return resource as KResource<TResource>
}
