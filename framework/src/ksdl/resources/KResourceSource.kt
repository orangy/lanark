package ksdl.resources

import ksdl.diagnostics.*

interface KResourceSource {
    fun findResource(path: String): KResource<*>
}

private val emptyProgress: (Double) -> Unit = {}

fun <TResource> KResourceSource.loadResource(path: String, resourceType: KResourceType): TResource {
    return findResource<TResource>(path, resourceType).load(emptyProgress)
}

fun <TResource> KResourceSource.findResource(path: String, resourceType: KResourceType): KResource<TResource> {
    val resource = findResource(path)
    if (resource.resourceType != resourceType)
        throw KPlatformException("Resource '$resource' is not a ${resourceType.name}")

    @Suppress("UNCHECKED_CAST")
    return resource as KResource<TResource>
}
